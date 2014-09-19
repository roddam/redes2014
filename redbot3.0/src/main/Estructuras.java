package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Estructuras {
	
	/**
	 * Variables de control
	 * 
	 */
	private static boolean debug = false; // Modo debug
	private static boolean depth = false; // Profundidad de procesamiento
	private static boolean persistent = false; // Uso de HTTP/1.1 o HTTP/1.0
	private static boolean pozos = false; // Respaldar URLs sin links de salida
	private static boolean multilang = false; // respaldar URLs en multiples idiomas
	private static boolean prx = false; // Uso de proxy
	private static boolean threads = false; // Uso de hilos

	/**
	 * Variables auxilares para las variables de control
	 */
	private static int profundidad = 5;
	private static String pozosFilename = "";
	private static String multilangFilename = "";
	private static URL proxyURL = null;
	private static int cantThreads = 1;

	/**
	 * Variables compartidas mutuo excluidas
	 */
	private static List<String> visitados = new LinkedList<String>();
	private static Set<String> setPozos = new HashSet<String>();
	private static Set<String> setMultilenguaje = new HashSet<String>();
	private static List<String> loops = new LinkedList<String>();
	private static List<String> urlsSinProcesar = new LinkedList<String>();
	private static List<HilosRedbot> hilosDisponibles = new ArrayList<HilosRedbot>();
	
	private static Estructuras e = null;
	
	/**
	 * Contructor privado
	 */
	private Estructuras() {
	}
	
	/**
	 * Obtengo la instancia del manejador de la estructura
	 */
	public static Estructuras getInstance() {
		if (Estructuras.e == null) {
			Estructuras.e = new Estructuras();
		}
		return Estructuras.e;
	}
	
	/**
	 * Metodo que inicializa las estructuras. En caso de producirse algun error se termina la ejecucion.
	 */
	public void inicializarEstructuras(String[] args) throws IllegalArgumentException, MalformedURLException {
		if (args.length > 0) {
			for (int i = 0; i < args.length - 2; i++) {
				String entrada = (String) args[i];
				switch (entrada) {
				case "-d":
					if (!debug) {
						debug = true;
					} else {
						throw new IllegalArgumentException("Parametro -d repetido.\n");
					}
					break;
				case "-depth":
					if (!depth) {
						depth = true;
						i++;
						int val = Integer.parseInt((String) args[i]);
						if (val > 0) {
							profundidad = val;
						} else {
							throw new IllegalArgumentException("La profundidad debe ser mayor a cero.");
						}
					} else {
						throw new IllegalArgumentException("Parametro -depth repetido.\n");
					}
					break;
				case "-persistent":
					if (!persistent) {
						persistent = true;
					} else {
						throw new IllegalArgumentException("Parametro -persistent repetido.\n");
					}
					break;
				case "-pozos":
					if (!pozos) {
						pozos = true;
						i++;
						pozosFilename = (String) args[i];
					} else {
						throw new IllegalArgumentException("Parametro -pozos repetido.\n");
					}
					break;
				case "-multilang":
					if (!multilang) {
						multilang = true;
						i++;
						multilangFilename = (String) args[i];
					} else {
						throw new IllegalArgumentException("Parametro -multilang repetido.\n");
					}
					break;
				case "-p":
					if (!threads) {
						i++;
						threads = true;
						int val = Integer.parseInt((String) args[i]);
						if (val > 1) {
							cantThreads = val;
						}
					} else {
						throw new IllegalArgumentException("Parametro -p repetido.\n");
					}
					break;
				case "-prx":
					if (!prx) {
						prx = true;
						i++;
						proxyURL = new URL(args[i]);
					} else {
						throw new IllegalArgumentException("Parametro -prx repetido.\n");
					}
					break;
				default:
					throw new IllegalArgumentException("Parametro incorrecto.\n");
				}
			}
		} else {
			throw new IllegalArgumentException("Faltan parametros.");
		}
		Estructuras.urlsSinProcesar.add(args[args.length - 1]);
		for (int i = 0; i < cantThreads; i++) {
			HilosRedbot h = new HilosRedbot(i, debug, depth, persistent, pozos, multilang, prx, 
					profundidad, pozosFilename, multilangFilename, proxyURL);
			Estructuras.hilosDisponibles.add(h);
		}
	}
	
	/**
	 * Metodo que inicia la ejecucion de los hilos de procesamiento
	 */
	public void iniciarHilosRedbot() {
		for (HilosRedbot h : hilosDisponibles) {
			h.start();
		}
	}
	
	/********************************************************************************************************************************
	 ******************************************* FUNCIONES MUTUO EXCLUIDAS **********************************************************
	 *******************************************************************************************************************************/
	
	public synchronized static void agregarVisitado(String urlNUeva) {
		Estructuras.visitados.add(urlNUeva);
	}

	public synchronized static boolean estaVisitado(String url) {
		return Estructuras.visitados.contains(url);
	}

	public synchronized static void agregarPozo(String pozo) {
		Estructuras.setPozos.add(pozo);
	}

	public synchronized static void agregarMultilenguaje(String  multilenguaje) {
		Estructuras.setMultilenguaje.add(multilenguaje);
	}

//	public synchronized static List<String> getLoops() {
//		return loops;
//	}
//
	public synchronized static void agregarLoop(String loop) {
		Estructuras.loops.add(loop);
	}

	public synchronized static String getUrlSinProcesar() {
		String result = null;
		if (!urlsSinProcesar.isEmpty()) {
			result = urlsSinProcesar.remove(0);
		}
		return result;
	}
	
	public synchronized static List<String> getUrlsSinProcesar() {
		return urlsSinProcesar;
	}

	public synchronized static void setUrlsSinProcesar(List<String> urlsSinProcesar) {
		Estructuras.urlsSinProcesar = urlsSinProcesar;
	}

	public synchronized static List<HilosRedbot> getHilosDisponibles() {
		return hilosDisponibles;
	}

	public synchronized static void setHilosDisponibles(List<HilosRedbot> hilosDisponibles) {
		Estructuras.hilosDisponibles = hilosDisponibles;
	}
	
	public synchronized static boolean hayHilosProcesando(int idHilo) {
		boolean result = false;
		for (HilosRedbot h : hilosDisponibles) {
			if (h.getId() != idHilo && h.isAlive()) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
		
//	public void procesarNodo(Nodo nodo, boolean soyThread) {
//		try {
//			/*
//			 * Obtengo la direccion IP y el puerto del host contra el que quiero
//			 * abrir el socket
//			 */
//			System.out.println(nodo.getUrl().toString());
//			InetAddress ia = InetAddress.getByName(nodo.getUrl().getHost());
//			int puerto = nodo.getUrl().getPort() == -1 ? Estructuras.puertoDefecto
//					: nodo.getUrl().getPort();
//
//			/*
//			 * Abro el socket y creo un flujo de datos para mandarle el mensaje
//			 * GET
//			 */
//			Socket sc = new Socket(ia, puerto);
//			PrintWriter pw = new PrintWriter(sc.getOutputStream());
//			pw.println(this.obtenerMensajeGET(nodo.getUrl()));
//			pw.flush();
//
//			/*
//			 * Abro un flujo de datos por el cual voy a recibir el mensaje de
//			 * respuesta enviado
//			 */
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					sc.getInputStream()));
//			String html = br.readLine();
//
//			/*
//			 * Primero leo el cabezal para verificar que esta todo bien y
//			 * analizar de que manera leer el mensaje de respuesta que se esta
//			 * enviando
//			 */
//			String cabezal = "";
//			Hashtable< String,String> hashCabezal = new Hashtable<String, String>();
//			
//			while (!html.isEmpty()) {
//				if(html.contains("HTTP/")){
//					String[] values = html.split(" ");
//					hashCabezal.put("CODE_ERROR",values[1].trim());
//					hashCabezal.put("CODE_MSG",values[2].trim());
//						
//				
//				}else if(html.startsWith("Content-Type")){
//					String[] values = html.split(":");
//					hashCabezal.put(values[0].trim(), values[1].trim());
//					
//				}else if(html.startsWith("Content-Language")){
//					String[] values = html.split(":");
//					hashCabezal.put(values[0].trim(), values[1].trim());
//					
//				}
//				else if(html.startsWith("ETag")){
//					String[] values = html.split(":");
//					hashCabezal.put(values[0].trim(), values[1].trim().replace("\"", ""));
//					
//				}
//				
//				cabezal = cabezal + "\n" + html;
//				html = br.readLine();
//			}
//			
//			if(soyThread){
//				System.out.println(" SOY UN THREADDDD !! "+nodo.getUrl());
//			}
//			
//			this.procesarCabezalResponse(cabezal);
//			if(hashCabezal.get("CODE_ERROR").equals("200") && hashCabezal.get("Content-Type").contains("text/html")){
//				html = br.readLine();
//				
//				this.patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
//				this.patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
//				this.patternMailTo = Pattern.compile(HTML_MAILTO_PATTERN);
//				while (html != null) {
//					this.matcherTag = this.patternTag.matcher(html);
//					while (this.matcherTag.find()) {
//						Nodo n = new Nodo(nodo.getNivel() + 1, null);
//						
//						String href = this.matcherTag.group(1); // href
//						if (href == null) {
//							System.out.println("HREF NULLL ");
//						}
//						this.matcherLink = this.patternLink.matcher(href);
//						while (this.matcherLink.find()) {
//							String link = this.matcherLink.group(1); // link
//							
//							link = replaceInvalidChar(link);
//							this.matcherMailTo = this.patternMailTo.matcher(link);
//							if (this.matcherMailTo.find()) {
//								String mailTo = this.matcherMailTo.group(1);
//								nodo.getListaMails().add(mailTo);
//							}else {
//								try {
//									if (!link.startsWith("http") && !link.startsWith("https")
//											&&!link.startsWith("#")){
//										if( !link.startsWith("/")){
//											link = nodo.getUrl().getProtocol() + "://"
//											+ nodo.getUrl().getHost() + ":"
//											+ puerto + "/"+link;
//										} else {
//											link = nodo.getUrl().getProtocol() + "://"
//													+ nodo.getUrl().getHost() + ":"
//													+ puerto +link;
//										}
//										
//										
//									}
//									
//									URL url = new URL(link);
//									String protocol = url.getProtocol();
//									if (!protocol.equals("https")) {
//										n.setUrl(url);
//										nodo.getAdyacentes().add(n);
//									}
//								} catch (MalformedURLException exp) {
//									//System.out.println(exp.getMessage());
//								}
//							}
//						}
//					}
//					html = br.readLine();
//				}
//			}else{
//				
//				System.out.println(hashCabezal.get("CODE_ERROR")+" ::: "+hashCabezal.get("CODE_MSG"));
//				
//			}
//			/*
//			 * luego de procesar el html, agrego la URL procesada a la lista de
//			 * URLs visitadas
//			 */
//			if (!this.visitados.contains(nodo.toString())) {
//				this.visitados.add(nodo.toString());
//			}
//			
//			/*
//			 * cierro el socket y el flujo de datos por el cual rescibia la
//			 * respuesta
//			 */
//			br.close();
//			sc.close();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void procesarCabezalResponse(String cabezal) {
//		System.out.println(cabezal);
//	}
//
//	public String obtenerMensajeGET(URL url) {
//	
//		String result = "GET ";
//		result = url.getPath().equals("") ? result + "/" : result
//				+ url.getPath();
//		result = this.persistent ? result + " HTTP/1.1\n" : result
//				+ " HTTP/1.0\n";
//		result = this.persistent ? result + "Connection: keep-alive\n" : result
//				+ "Connection: close\n";
//		result = result + "Host: " + url.getHost();
//		result = result + "\n";
//		result = result + "Accept: text/html, text/plain\n";
//
//		return result;
//	}
//	
//	private String replaceInvalidChar(String link) {
//		link = link.replaceAll("'", "");
//		link = link.replaceAll("\"", "");
//		
//		return link;
//	}
}
	/*
	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_MAILTO_PATTERN = "mailto:([^?]+)";

	private Pattern patternTag;
	private Pattern patternLink;
	private Pattern patternMailTo;
	private Matcher matcherTag;
	private Matcher matcherLink;
	private Matcher matcherMailTo;
	*/