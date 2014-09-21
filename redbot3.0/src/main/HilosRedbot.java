package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HilosRedbot extends Thread {

	public static final int puertoDefecto = 80;
	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_MAILTO_PATTERN = "mailto:([^?]+)";

	private Pattern patternTag;
	private Pattern patternLink;
	private Pattern patternMailTo;
	private Matcher matcherTag;
	private Matcher matcherLink;
	private Matcher matcherMailTo;

	private int idHilo;
	private Estructuras estructuras;

	private boolean debug;
	private boolean persistent;

	private boolean depth;
	private int profundidad;

	private boolean pozos;
	private String pozosFilename;

	private boolean multilang;
	private String multilangFilename;

	private boolean prx;
	private URL proxyURL;

	/**
	 * Constructor donde recibe los parametros necesarios para el procesamiento
	 * durante su ejecucion
	 */
	public HilosRedbot(int idHilo, boolean debug, boolean depth,
			boolean persistent, boolean pozos, boolean multilang, boolean prx,
			int profundidad, String pozosFilename, String multilangFilename,
			URL proxyURL, Estructuras estructuras) {
		super();
		this.idHilo = idHilo;
		this.estructuras = estructuras;
		this.debug = debug;
		this.persistent = persistent;
		this.depth = depth;
		this.profundidad = profundidad;
		this.pozos = pozos;
		this.pozosFilename = pozosFilename;
		this.multilang = multilang;
		this.multilangFilename = multilangFilename;
		this.prx = prx;
		this.proxyURL = proxyURL;
	}

	/**
	 * Metodo auxiliar para imprimir los mails encontrados en un nodo
	 */
	public void imprimirMail(Nodo nodo) {
		for (String s : nodo.getListaMails()) {
			System.out.println(s);
		}
	}

	/**
	 * Creo el mensaje GET dependiendo de las variables de control
	 */
	public String obtenerMensajeGET(URL url) {
		String result = "GET ";
		String path = url.getPath().equals("") ? "/" : url.getPath();
		if (this.prx) {
			result = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + path;
		} else {
			result = result + path;
		}
		result = this.persistent ? result + " HTTP/1.1\n" : result + " HTTP/1.0\n";
		result = this.persistent ? result + "Connection: keep-alive\n" : result + "Connection: close\n";
		result = result + "Host: " + url.getHost();
		result = result + "\n";
		result = result + "Accept: text/html\n";
		return result;
	}

	/**
	 * Metodo para procesar solo el cabezal.
	 */
	public Hashtable<String, String> procesarCabezal(BufferedReader br, Nodo nodo) throws IOException {
		String html = br.readLine();
		Hashtable<String, String> hashCabezal = new Hashtable<String, String>();
		while (!html.isEmpty()) {
			if (html.contains("HTTP/")) {
				String[] values = html.split(" ");
				hashCabezal.put("CODE_ERROR", values[1].trim());
				hashCabezal.put("CODE_MSG", values[2].trim());
			} else if (html.startsWith("Content-Type")) {
				String[] values = html.split(":");
				hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("Content-Language")) {
				estructuras.agregarMultilenguaje(nodo.toString());
				String[] values = html.split(":");
				hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("ETag")) {
				String[] values = html.split(":");
				hashCabezal.put(values[0].trim(), values[1].trim().replace("\"", ""));
			}
			html = br.readLine();
		}
		return hashCabezal;
	}

	/**
	 * Genero una URL a partir del string encontrado durante el procesamiento
	 */
	public URL stringToURL(String string, URL url, int puerto) throws MalformedURLException {
		URL urlGenerada = null;
		if (!string.startsWith("http") && !string.startsWith("https") && !string.startsWith("#")) {
			if (!string.startsWith("/")) {
				string = url.getProtocol() + "://" + url.getHost() + ":" + puerto + "/" + string;
			} else {
				string = url.getProtocol() + "://" + url.getHost() + ":" + puerto + string;
			}
		}
		urlGenerada = new URL(string);
		return urlGenerada;
	}

	private String replaceInvalidChar(String link) {
		link = link.replaceAll("'", "");
		link = link.replaceAll("\"", "");
		return link;
	}

	/**
	 * Intento abrir el socket contra el IP:puerto que me pasan
	 */
	public Socket obtenerSocket(InetAddress ia, int puerto) {
		Socket sc = null;
		try {
			sc = new Socket(ia, puerto);
		} catch (IOException e) {
			System.out.println("Se produjo un error inesperado al intentar abrir el socket contra " + ia.toString() + ":" + puerto);
		} catch (Exception e) {
			System.out.println("Se produjo un error inesperado al intentar abrir el socket contra " + ia.toString() + ":" + puerto);
		}
		return sc;
	}

	public void procesarNodo(Nodo nodo) {
		try {
			InetAddress ia;
			int puerto;
			if (prx) {
				/*
				 * Si me pasaron el switch -prx el socket lo voy a abrir contra
				 * el proxy, entonces el ip y puerto con el que creo el socket
				 * tienen que ser los del proxy.
				 */
				ia = InetAddress.getByName(proxyURL.getHost());
				puerto = proxyURL.getPort() == -1 ? puertoDefecto : proxyURL.getPort();
			} else {
				// Si no me pasaron el switch -prx el socket lo voy a abrir
				// directamente contra el host, entonces el ip y puerto con el
				// que creo el socket tienen que ser los del host.
				ia = InetAddress.getByName(nodo.getUrl().getHost());
				puerto = nodo.getUrl().getPort() == -1 ? puertoDefecto : nodo.getUrl().getPort();
			}
			// Obtengo el socket y creo un flujo de datos para mandarle el
			// mensaje GET
			Socket sc = obtenerSocket(ia, puerto);
			if (sc != null) {
				PrintWriter pw = new PrintWriter(sc.getOutputStream());
				pw.println(this.obtenerMensajeGET(nodo.getUrl()));
				pw.flush();

				// Abro un flujo de datos por el cual voy a recibir el mensaje
				// de respuesta enviado
				BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
				// Primero leo el cabezal para verificar que esta todo bien y
				// analizar de que manera leer el mensaje de respuesta que se
				// esta enviando
				Hashtable<String, String> hashCabezal = procesarCabezal(br, nodo);
				if (hashCabezal.get("CODE_ERROR").equals("200") && hashCabezal.get("Content-Type").contains("text/html")) {
					String html = br.readLine();
					this.patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
					this.patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
					this.patternMailTo = Pattern.compile(HTML_MAILTO_PATTERN);

					// FIXME hay que leer hasta que llegue al tamaño o patron
					// 0\n\n mas o menos

					while (html != null) {
						this.matcherTag = this.patternTag.matcher(html);
						while (this.matcherTag.find()) {
							// me quedo con todo el href
							String href = this.matcherTag.group(1);
//							if (href == null) {
//								System.out.println("HREF NULLL ");
//							}
							this.matcherLink = this.patternLink.matcher(href);
							while (this.matcherLink.find()) {
								// me quedo solo con el link
								String link = this.matcherLink.group(1);
								// Saco las comillas que sobran
								link = replaceInvalidChar(link);
								this.matcherMailTo = this.patternMailTo.matcher(link);
								if (this.matcherMailTo.find()) {
									String mailTo = this.matcherMailTo.group(1);
									nodo.getListaMails().add(mailTo);
								} else {
									try {
										URL url = stringToURL(link, nodo.getUrl(), puerto);
										String protocol = url.getProtocol();
										if (!protocol.equals("https")) {
											Nodo n = new Nodo(nodo.getNivel() + 1, null);
											n.setUrl(url);
											if (estructuras.estaVisitado(n.toString())) {
												estructuras.encontrarLoops(nodo, n, nodo);
											} else {
												nodo.getAdyacentes().add(n);
												n.getPadres().add(nodo);
											}
											estructuras.setUrlsSinProcesar(n);
										}
									} catch (MalformedURLException exp) {
										 System.out.println("Encontre una URL mal formada y no la voy procesar.");
									}
								}
							}
						}
						html = br.readLine();
					}
					if (nodo.getAdyacentes().isEmpty()) {
						estructuras.agregarPozo(nodo.toString());
					}
				} else {
					// System.out.println(hashCabezal.get("CODE_ERROR") +
					// " ::: " + hashCabezal.get("CODE_MSG"));
				}
				// cierro el socket y el flujo de datos por el cual recibia la respuesta
				br.close();
				sc.close();
			}
		} catch (Exception e) {
			System.out.println("Se ha producido un error inesperado.");
		}
	}

	/**
	 * Metodo que realiza todo el procesamiento de una URL
	 */
	@Override
	public void run() {
		// Obtengo una URL para procesar
		while (!Estructuras.seTerminoProcesamiento) {
			Nodo nodoSinProcesar = estructuras.getUrlSinProcesar(this.idHilo);
			if (nodoSinProcesar != null) {
				if (!estructuras.estaVisitado(nodoSinProcesar.toString())) {
					estructuras.agregarVisitado(nodoSinProcesar.toString());
					if (nodoSinProcesar.getNivel() <= profundidad) {
						// Si hay una URL disponible, la proceso.
						// Actualizo los pozos, los loops, los visitados, los multilenguaje
						// Imprimo los mails que vaya encontrando
						// Agrego a la lista de URLs sin procesar las URL que vaya encontrando
						// Despierto a los hilos que pudiesen estar esperando que yo termine de procesar.
						procesarNodo(nodoSinProcesar);
						imprimirMail(nodoSinProcesar);
					} else {
						 System.out.println("ME DIERON UNA URL QUE NO VOY A PROCESAR POR PROFUNDIDAD");
					}
				}
			}
			// Despues de procesar me fijo si no hay ninguna URL para procesar y
			// si hay algun hilo ejecutando que no sea yo mismo
			if (estructuras.nodosSinPocesarEsVacia()) {
				if (estructuras.hayOtrosHilosEjecutand(this.idHilo)) {
					// Si no hay ninguna URL para procesar y hay algun hilo ejecutando,
					// podrian aparecer mas URLs a procesar.
					// Entonces me duermo, esperando que alguno de los hilos que
					// estan procesando me despierte.
					estructuras.dormirHilo(this.idHilo);
				} else {
					// Si no hay ninguna URL para procesar y no hay hilos procesando,
					// entonces soy el ultimo que quedo procesando y no genere ninguna URL nueva.
					// Entonces fin procesamiento del hilo.
					estructuras.finalizarHilos();
				}
			}
		}
		estructuras.termineDeEjecutar(this.idHilo);
		System.out.println(this.idHilo + " TERMINE DE EJECTUAR!!!!");
	}

	public int getIdHilo() {
		return idHilo;
	}

	public void setIdHilo(int idHilo) {
		this.idHilo = idHilo;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDepth() {
		return depth;
	}

	public void setDepth(boolean depth) {
		this.depth = depth;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isPozos() {
		return pozos;
	}

	public void setPozos(boolean pozos) {
		this.pozos = pozos;
	}

	public boolean isMultilang() {
		return multilang;
	}

	public void setMultilang(boolean multilang) {
		this.multilang = multilang;
	}

	public boolean isPrx() {
		return prx;
	}

	public void setPrx(boolean prx) {
		this.prx = prx;
	}

	public int getProfundidad() {
		return profundidad;
	}

	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
	}

	public String getPozosFilename() {
		return pozosFilename;
	}

	public void setPozosFilename(String pozosFilename) {
		this.pozosFilename = pozosFilename;
	}

	public String getMultilangFilename() {
		return multilangFilename;
	}

	public void setMultilangFilename(String multilangFilename) {
		this.multilangFilename = multilangFilename;
	}

	public URL getProxyURL() {
		return proxyURL;
	}

	public void setProxyURL(URL proxyURL) {
		this.proxyURL = proxyURL;
	}
}
