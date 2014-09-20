package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


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
	 * 
	 * @param debug
	 * @param depth
	 * @param persistent
	 * @param pozos
	 * @param multilang
	 * @param prx
	 * @param profundidad
	 * @param pozosFilename
	 * @param multilangFilename
	 * @param proxyURL
	 */
	public HilosRedbot(int idHilo, boolean debug, boolean depth,
			boolean persistent, boolean pozos, boolean multilang, boolean prx,
			int profundidad, String pozosFilename, String multilangFilename,
			URL proxyURL) {
		super();
		this.idHilo = idHilo;
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
	public Hashtable< String, String> procesarCabezal(BufferedReader br) throws IOException {
		String html = br.readLine();
		Hashtable<String, String> hashCabezal = new Hashtable<String, String>();
		
		while (!html.isEmpty()) {
			if (html.contains("HTTP/")) {
				 String[] values = html.split(" ");
				 hashCabezal.put("CODE_ERROR",values[1].trim());
				 hashCabezal.put("CODE_MSG",values[2].trim());
			} else if (html.startsWith("Content-Type")) {
				 String[] values = html.split(":");
				 hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("Content-Language")) {
				 String[] values = html.split(":");
				 hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("ETag")) {
				 String[] values = html.split(":");
				 hashCabezal.put(values[0].trim(),
				 values[1].trim().replace("\"", ""));
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
		string = string.replaceAll("'", "");
		string = string.replaceAll("\"", "");
		if (!string.startsWith("http") && !string.startsWith("https") && !string.startsWith("#")){
			if( !string.startsWith("/")){
				string = url.getProtocol() + "://" + url.getHost() + ":" + puerto + "/"+ string;
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
	 * Metodos para probar algo
	 */
	public static boolean semaforoUrlsSinProcesar = true;
	public synchronized Nodo getUrlSinProcesar() {
		System.out.println("Soy el hilo " + this.idHilo + " y quiero una URL");
		if (!semaforoUrlsSinProcesar) {
			try {
				System.out.println("Soy el hilo " + this.idHilo + " y me vopy a dormir");
				wait();
				System.out.println("Soy el hilo " + this.idHilo + " y me despertaron");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Nodo url = Estructuras.getUrlSinProcesar();
		semaforoUrlsSinProcesar = false;
//		System.out.println("ANTES NOTIFY GET " + this.idHilo);
//		notifyAll();
//		System.out.println("DESPUES NOTIFY GET " + this.idHilo);
		return url;
	}
	
	public synchronized void setUrlsSinProcesar(Nodo n){
		System.out.println("Soy el hilo " + this.idHilo + " y voy a agregar la URL con profundidad " + n.getNivel() + " ### " + n.toString());
		Estructuras.setUrlsSinProcesar(n);
		semaforoUrlsSinProcesar = true;
		System.out.println("ANTES NOTIFY PUT " + this.idHilo);
		notifyAll();
		System.out.println("DESPUES NOTIFY PUT " + this.idHilo);
	}
	
	public synchronized boolean quedanNodosPorProcesar() {
		return Estructuras.quedanNodosPorProcesar();
	}
	
	public synchronized boolean hayHilosProcesando(int idHilo) {
		return hayHilosProcesando(this.idHilo);
	}
	
	public synchronized boolean estaVisitado(String s) {
		return Estructuras.estaVisitado(s);
	}
	
	public synchronized void agregarVisitado(String s) {
		Estructuras.agregarVisitado(s);
	}
	/*********************************************/

	public void procesarNodo(Nodo nodo) {
		try {
			/*
			 * Obtengo la direccion IP y el puerto del host contra el que quiero
			 * abrir el socket
			 */
			System.out.println(nodo.getUrl().toString());
			InetAddress ia = InetAddress.getByName(nodo.getUrl().getHost());
			int puerto = nodo.getUrl().getPort() == -1 ? puertoDefecto : nodo
					.getUrl().getPort();

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

			/*
			 * Primero leo el cabezal para verificar que esta todo bien y
			 * analizar de que manera leer el mensaje de respuesta que se esta
			 * enviando
			 */
			Hashtable<String, String> hashCabezal = procesarCabezal(br);

			if (hashCabezal.get("CODE_ERROR").equals("200")
					&& hashCabezal.get("Content-Type").contains("text/html")) {
				String html = br.readLine();

				this.patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
				this.patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
				this.patternMailTo = Pattern.compile(HTML_MAILTO_PATTERN);
				while (html != null) {
					this.matcherTag = this.patternTag.matcher(html);
					while (this.matcherTag.find()) {
						Nodo n = new Nodo(nodo.getNivel() + 1, null);

						String href = this.matcherTag.group(1); // href
						if (href == null) {
							System.out.println("HREF NULLL ");
						}
						this.matcherLink = this.patternLink.matcher(href);
						while (this.matcherLink.find()) {
							String link = this.matcherLink.group(1); // link

							link = replaceInvalidChar(link);
							this.matcherMailTo = this.patternMailTo
									.matcher(link);
							if (this.matcherMailTo.find()) {
								String mailTo = this.matcherMailTo.group(1);
								nodo.getListaMails().add(mailTo);
							} else {
								try {
									if (!link.startsWith("http")
											&& !link.startsWith("https")
											&& !link.startsWith("#")) {
										if (!link.startsWith("/")) {
											link = nodo.getUrl().getProtocol()
													+ "://"
													+ nodo.getUrl().getHost()
													+ ":" + puerto + "/" + link;
										} else {
											link = nodo.getUrl().getProtocol()
													+ "://"
													+ nodo.getUrl().getHost()
													+ ":" + puerto + link;
										}

									}

									URL url = new URL(link);
									String protocol = url.getProtocol();
									if (!protocol.equals("https")) {
										n.setUrl(url);
										nodo.getAdyacentes().add(n);
										// FIXME
										setUrlsSinProcesar(n);
									}
								} catch (MalformedURLException exp) {
									// System.out.println(exp.getMessage());
								}
							}
						}
					}
					html = br.readLine();
				}
			} else {
				
				System.out.println(hashCabezal.get("CODE_ERROR") + " ::: "
						+ hashCabezal.get("CODE_MSG"));

			}
			/*
			 * luego de procesar el html, agrego la URL procesada a la lista de
			 * URLs visitadas
			 */
			// FIXME
			if (!estaVisitado(nodo.toString())) {
				System.out.println("Hay que ver porque esta visitado " + nodo.toString());
				agregarVisitado(nodo.toString());
			}

			/*
			 * cierro el socket y el flujo de datos por el cual rescibia la
			 * respuesta
			 */
			br.close();
			sc.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que realiza todo el procesamiento de una URL
	 */
	@Override
	public void run() {
		// Obtengo una URL para procesar
		boolean faltaProcesar = true;
		while (faltaProcesar) {
			//FIXME
			Nodo nodoSinProcesar = getUrlSinProcesar();
			if (nodoSinProcesar != null) {
				semaforoUrlsSinProcesar = true;
				if(nodoSinProcesar.getNivel() <= profundidad) {
					procesarNodo(nodoSinProcesar);
				} else {
					System.out.println("mE DIERON UNA url QUE NO VOY A PROCESAR POR PROFUNDIDAD");
				}
				// Si hay una URL disponible, la proceso.
				// Actualizo los pozos, los loops, los visitados, los
				// multilenguaje
				// Imprimo los mails que vaya encontrando
				// Agrego a la lista de URLs sin procesar las URL que vaya
				// encontrando
				// Despierto a los hilos que pudiesen estar esperando que yo
				// termine de procesar.
				imprimirMail(nodoSinProcesar);
			} else {
				System.out.println("No me dieron nada");
			}
			// Despues de procesar me fijo si no hay ninguna URL para procesar y
			// si hay algun hilo ejecutando que no sea yo mismo
			//FIXME
//			if (!quedanNodosPorProcesar()){
//				if( hayHilosProcesando(this.idHilo)) {
	//				// Si no hay ninguna URL para procesar y hay algun hilo
	//				// ejecutando, podrian aparecer mas URLs a procesar.
	//				// Entonces me duermo, esperando que alguno de los hilos que
	//				// estan procesando me despierte.
	//				// FIXME me tengo que dormir
//					System.out.println("Dice rodrigo que esto no se imprime.");
//					try {
//						wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}else {
//					// Si no hay ninguna URL para procesar y no hay hilos
//					// procesando,
//					// entonces soy el ultimo que quedo procesando y no genere
//					// ninguna URL nueva.
//					// Entonces fin procesamiento del hilo.
//					faltaProcesar = false;
//					System.out.println("Termine de procesar :D : " + this.idHilo);
//				}
//			} 
		}
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
	public void imprimirMail(Nodo nodo){
		for(String s:nodo.getListaMails()) {
			System.out.println(s);
		}
	}
}
