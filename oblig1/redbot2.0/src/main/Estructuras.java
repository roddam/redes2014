package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estructuras {

	/**
	 * Variables para el parseo del html
	 */
	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_MAILTO_PATTERN = "mailto:([^?]+)";

	private Pattern patternTag;
	private Pattern patternLink;
	private Pattern patternMailTo;
	private Matcher matcherTag;
	private Matcher matcherLink;
	private Matcher matcherMailTo;

	/**
	 * Instancia unica de la clase que maneja las estructuras
	 */
	private static Estructuras e = null;

	/**
	 * Vaiables por defecto
	 * 
	 */
	public static final int puertoDefecto = 80;
	public static final int profDefecto = 5;

	/**
	 * mensaje de error en caso que se ingrese algún switch incorrectamente.
	 */
	private static String mensajeErrorUsage = "Usage: redbot [-d] [-depth N]\n"
			+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
			+ "[-p threads] [-prx proxyURL] URL";

	/**
	 * Nodo que contiene la URL a partir de la cual se realiza el procesamiento.
	 */
	private Nodo raiz;

	/**
	 * Lista con las URLs que ya fueron procesadas.
	 */
	private List<String> visitados;

	/**
	 * Set con las URLs que no tiene links de salida.
	 */
	private Set<String> listaURLsPozos;

	/**
	 * Variables de control. Habilitada = true Deshabilitada = false Utilizadas
	 * durante el procesamiento.
	 */
	private boolean debug;
	private boolean depth;
	private boolean persistent;
	private boolean pozos;
	private boolean multilang;
	private boolean prx;
	public static boolean threads = false;

	/**
	 * Variables auxiliares utilizadas para el procesamiento a realizar
	 * dependiendo de las variables de control
	 */
	private int profundidad;
	private String pozosFilename;
	private String multilangFilename;
	private String proxyURL;
	public static int cantThreads = 0;

	private Estructuras() {
		this.raiz = null;
		this.visitados = new LinkedList<String>();
		this.debug = false;
		this.depth = false;
		this.persistent = false;
		this.pozos = false;
		this.multilang = false;
		this.prx = false;
		this.profundidad = Estructuras.profDefecto;
		this.pozosFilename = null;
		this.multilangFilename = null;
		this.proxyURL = null;
	}

	public static Estructuras getInstance() {
		if (Estructuras.e == null) {
			Estructuras.e = new Estructuras();
		}
		return Estructuras.e;
	}

	public void inicializarEstructuras(String[] args)
			throws MalformedURLException, Exception {
		if (args.length > 0) {
			for (int i = 0; i < args.length - 2; i++) {
				String entrada = (String) args[i];
				switch (entrada) {
				case "-d":
					if (!this.debug) {
						this.debug = true;
					} else {
						throw new Exception("Parametro -d repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-depth":
					if (!this.depth) {
						this.depth = true;
						i++;
						this.profundidad = Integer.parseInt((String) args[i]);
					} else {
						throw new Exception("Parametro -depth repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-persistent":
					if (!this.persistent) {
						this.persistent = true;
					} else {
						throw new Exception("Parametro -persistent repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-pozos":
					if (!this.pozos) {
						this.pozos = true;
						i++;
						this.pozosFilename = (String) args[i];
					} else {
						throw new Exception("Parametro -pozos repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-multilang":
					if (!this.multilang) {
						i++;
						this.multilang = true;
						this.multilangFilename = (String) args[i];
					} else {
						throw new Exception("Parametro -multilang repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-p":
					if (!threads) {
						i++;
						threads = true;
						cantThreads = Integer.parseInt((String) args[i]);
					} else {
						throw new Exception("Parametro -p repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				case "-prx":
					if (!this.prx) {
						i++;
						this.prx = true;
						this.proxyURL = (String) args[i];
					} else {
						throw new Exception("Parametro -prx repetido.\n"
								+ Estructuras.mensajeErrorUsage);
					}
					break;
				default:
					throw new Exception("Parametro incorrecto"
							+ Estructuras.mensajeErrorUsage);
				}
			}
			this.raiz = new Nodo();
			raiz.setProf(0);
			raiz.setUrl(new URL(args[args.length - 1]));
		} else {
			throw new Exception(Estructuras.mensajeErrorUsage);
		}
	}

	public void procesarURL(Nodo nodo, int prof) {
		this.procesarNodo(nodo);
		for (Nodo n : nodo.getAdyacentes()) {
			if (!this.visitados.contains(n.toString())) {
				if (prof <= this.profundidad) {
					if (Estructuras.cantThreads > 0) {
						/*
						 * Si tengo hilos disponibles, creo uno y le paso el
						 * nodo que tiene que procesar junto con la profundidad
						 * en la que ese nodo estaria.
						 */
						Estructuras.cantThreads--;
						new ThreadRedbot(n, prof + 1).start();
						this.procesarNodo(nodo);
						Estructuras.cantThreads++;
					}
					this.procesarURL(n, prof + 1);
				}
			}
		}
	}

	public void procesarNodo(Nodo nodo) {
		try {
			/*
			 * Obtengo la direccion IP y el puerto del host contra el que quiero
			 * abrir el socket
			 */
			InetAddress ia = InetAddress.getByName(nodo.getUrl().getHost());
			int puerto = nodo.getUrl().getPort() == -1 ? Estructuras.puertoDefecto
					: nodo.getUrl().getPort();

			/*
			 * Abro el socket y creo un flujo de datos para mandarle el mensaje
			 * GET
			 */
			Socket sc = new Socket(ia, puerto);
			PrintWriter pw = new PrintWriter(sc.getOutputStream());
			pw.println(this.obtenerMensajeGET(nodo.getUrl()));
			pw.flush();

			/*
			 * Abro un flujo de datos por el cual voy a recibir el mensaje de
			 * respuesta enviado
			 */
			BufferedReader br = new BufferedReader(new InputStreamReader(
					sc.getInputStream()));
			String html = br.readLine();

			/*
			 * Primero leo el cabezal para verificar que esta todo bien y
			 * analizar de que manera leer el mensaje de respuesta que se esta
			 * enviando
			 */
			String cabezal = "";
			while (!html.isEmpty()) {
				cabezal = cabezal + "\n" + html;
				html = br.readLine();
			}
			this.procesarCabezalResponse(cabezal);

			this.patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
			this.patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
			this.patternMailTo = Pattern.compile(HTML_MAILTO_PATTERN);
			while (html != null) {
				this.matcherTag = this.patternTag.matcher(html);
				while (this.matcherTag.find()) {
					Nodo n = new Nodo();
					n.setProf(nodo.getProf() + 1);
					String href = this.matcherTag.group(1); // href
					this.matcherLink = this.patternLink.matcher(href);
					while (this.matcherLink.find()) {
						String link = this.matcherLink.group(1); // link
						link = replaceInvalidChar(link);
						this.matcherMailTo = this.patternMailTo.matcher(link);
						if (this.matcherMailTo.find()) {
							String mailTo = this.matcherMailTo.group(1);
							nodo.getMailsEncontrados().add(mailTo);
						}
						try {
							URL url = new URL(link);
							String protocol = url.getProtocol();
							if (!protocol.equals("https")) {
								n.setUrl(url);
								nodo.getAdyacentes().add(n);
							}
						} catch (MalformedURLException exp) {
							if (exp.getMessage().contains("no protocol")) {
								link = nodo.getUrl().getProtocol() + "://"
										+ nodo.getUrl().getHost() + ":"
										+ puerto + link;
								n.setUrl(new URL(link));
								nodo.getAdyacentes().add(n);
							}
						}
					}
				}
				html = br.readLine();
			}
			/*
			 * luego de procesar el html, agrego la URL procesada a la lista de
			 * URLs visitadas
			 */
			if (!this.visitados.contains(nodo.toString())) {
				this.visitados.add(nodo.toString());
			}

			/*
			 * cierro el socket y el flujo de datos por el cual rescibia la
			 * respuesta
			 */
			br.close();
			sc.close();

			/* Imprimo los mails encontrados en el html */
			this.imprimirMails(nodo);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Funcion auxiliar para procesar el cabezal del mensaje obtenido
	 */
	public void procesarCabezalResponse(String cabezal) {
		System.out.println(cabezal);
	}

	public String obtenerMensajeGET(URL url) {
		String result = "GET ";
		result = url.getPath().equals("") ? result + "/" : result
				+ url.getPath();
		result = this.persistent ? result + " HTTP/1.1\n" : result
				+ " HTTP/1.0\n";
		result = this.persistent ? result + "Connection: keep-alive\n" : result
				+ "Connection: close\n";
		result = result + "Host: " + url.getHost();
		result = result + "\n";
		result = result + "Accept: text/html, text/plain\n";

		return result;
	}
	
	/**
	 * Funcion Auxiliar utilizada para imprimir los mails encontrados en una URL
	 */
	public void imprimirMails(Nodo n) {
		// FIXME evitar concurrencia
		int i = 1;
		for (String s : n.getMailsEncontrados()) {
			System.out.println(i++ + " - " + s);
		}
	}

	/**
	 * 
	 */
	private String replaceInvalidChar(String link) {
		link = link.replaceAll("'", "");
		link = link.replaceAll("\"", "");
		if (!link.startsWith("http") && !link.startsWith("https")
				&& !link.startsWith("/"))
			link = "/" + link;
		return link;
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

	public int getCantThreads() {
		return cantThreads;
	}

	public String getProxyURL() {
		return proxyURL;
	}

	public void setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
	}

	public List<String> getVisitados() {
		return visitados;
	}

	public void setVisitados(List<String> visitados) {
		this.visitados = visitados;
	}

	public Nodo getRaiz() {
		return raiz;
	}

	public void setRaiz(Nodo raiz) {
		this.raiz = raiz;
	}

	public Set<String> getListaURLsPozos() {
		return listaURLsPozos;
	}

	public void setListaURLsPozos(Set<String> listaURLsPozos) {
		this.listaURLsPozos = listaURLsPozos;
	}
}
