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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estructuras {

	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_MAILTO_PATTERN = "mailto:([^?]+)";

	private Pattern patternTag, patternLink, patternMailTo;
	private Matcher matcherTag, matcherLink, matcherMailTo;

	/**
	 * Instancia unica de la clase que maneja las estructuras
	 */
	private static Estructuras e = null;
	public static final int puertoDefecto = 80;
	public static final int profDefecto = 5;

	private static String mensajeErrorUsage = "Usage: redbot [-d] [-depth N]\n"
			+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
			+ "[-p threads] [-prx proxyURL] URL";

	/**
	 * Lista que contiene las URLs a procesar
	 */
	private Nodo raiz;

	/**
	 * HashMap para saber en que nivel esta una URL
	 */
	private HashMap<String, Integer> nivelPorURL;

	/**
	 * Lista con las URLs que ya fueron procesadas.
	 */
	private List<String> visitados;

	/**
	 * Lista de mails que se van encontrando mientras se realiza el
	 * procesamiento
	 */
	private List<String> listaMails;
	
	private Set<String> listaURLsPozos;

	/**
	 * Variables de control. Habilitada = true Deshabilitada = false
	 */
	private boolean debug;
	private boolean depth;
	private boolean persistent;
	private boolean pozos;
	private boolean multilang;
	public static boolean threads = false;
	private boolean prx;

	/**
	 * Variables auxiliares utilizadas para el procesamiento a realizar
	 * dependiendo de las variables de control
	 */
	private int profundidad;
	private String pozosFilename;
	private String multilangFilename;
	public static int cantThreads = 0;
	private String proxyURL;

	private Estructuras() {
		this.raiz = null;
		this.nivelPorURL = new HashMap<String, Integer>();
		this.visitados = new LinkedList<String>();
		this.listaMails = new LinkedList<String>();
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

	public void inicializarEstructuras(String[] args) throws Exception {
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
					// FIXME si hay hilos disponibles le doy el nodo a procesar
					// a
					// uno de esos, si no lo agarro yo.
					if (Estructuras.cantThreads > 0) {
						new Thread().start();
						this.procesarNodo(nodo);
					}
					this.procesarURL(n, prof + 1);
				}
			}
		}
	}

	public void procesarNodo(Nodo nodo) {
		try {
			InetAddress ia = InetAddress.getByName(nodo.getUrl().getHost());
			int puerto = nodo.getUrl().getPort() == -1 ? Estructuras.puertoDefecto
					: nodo.getUrl().getPort();
			Socket sc = new Socket(ia, puerto);
			PrintWriter pw = new PrintWriter(sc.getOutputStream());
//			System.out.println(this.obtenerMensajeGET(nodo.getUrl()));
			pw.println(this.obtenerMensajeGET(nodo.getUrl()));
			pw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			String html = br.readLine();
			System.out.println("######################################");
			if (System.getProperty("line.separator").equals("\r\n")) {
				System.out.println("tu vieja");
			}
			System.out.println("######################################");

			String cabezal = "";
			while (!html.isEmpty()) {
				cabezal = cabezal + "\n" + html;
				html = br.readLine();
			}
			
			this.procesarCabezalResponse(cabezal);
			
			
			patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
			patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
			patternMailTo = Pattern.compile(HTML_MAILTO_PATTERN);
			while (html != null) {
				matcherTag = patternTag.matcher(html);
				while (matcherTag.find()) {
					Nodo n = new Nodo();
					n.setProf(nodo.getProf() + 1);
					String href = matcherTag.group(1); // href
					matcherLink = patternLink.matcher(href);
					while (matcherLink.find()) {
						String link = matcherLink.group(1); // link
						link = replaceInvalidChar(link);
						matcherMailTo = patternMailTo.matcher(link);
						if (matcherMailTo.find()) {
							String mailTo = matcherMailTo.group(1);
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
								link = nodo.getUrl().getProtocol() + "://"+ nodo.getUrl().getHost() + ":"+ puerto + link;
								n.setUrl(new URL(link));
								nodo.getAdyacentes().add(n);
							}
						}
					}
				}
				html = br.readLine();
			}
			if (!this.visitados.contains(nodo.toString())) {
				this.visitados.add(nodo.toString());
			}
			br.close();
			sc.close();
			this.imprimirMails(nodo);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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

	public void imprimirMails(Nodo n) {
		// FIXME evitar concurrencia
		int i = 1;
		for (String s : n.getMailsEncontrados()) {
			System.out.println(i++ + " - " + s);
		}
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

	public List<String> getListaMails() {
		return listaMails;
	}

	public void setListaMails(List<String> listaMails) {
		this.listaMails = listaMails;
	}

	public HashMap<String, Integer> getNivelPorURL() {
		return nivelPorURL;
	}

	public void setNivelPorURL(HashMap<String, Integer> nivelPorURL) {
		this.nivelPorURL = nivelPorURL;
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
	
	private String replaceInvalidChar(String link) {
		link = link.replaceAll("'", "");
		link = link.replaceAll("\"", "");
		if (!link.startsWith("http") && !link.startsWith("https")
				&& !link.startsWith("/"))
			link = "/" + link;
		return link;
	}	
}
