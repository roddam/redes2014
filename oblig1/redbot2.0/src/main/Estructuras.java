package main;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Estructuras {

	/**
	 * Instancia unica de la clase que maneja las estructuras
	 */
	private static Estructuras e = null;
	public static int puertoDefecto = 80;
	private static String mensajeErrorUsage = "Usage: redbot [-d] [-depth N]\n"
			+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
			+ "[-p threads] [-prx proxyURL] URL";

	/**
	 * Lista que contiene las URLs a procesar
	 */
	private List<String> listaURLs;
	
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

	/**
	 * Variables de control. Habilitada = true Deshabilitada = false
	 */
	private boolean debug;
	private boolean depth;
	private boolean persistent;
	private boolean pozos;
	private boolean multilang;
	private boolean threads;
	private boolean prx;

	/**
	 * Variables auxiliares utilizadas para el procesamiento a realizar
	 * dependiendo de las variables de control
	 */
	private int profundidad;
	private String pozosFilename;
	private String multilangFilename;
	private int cantThreads;
	private String proxyURL;

	private Estructuras() {
		this.listaURLs = new LinkedList<String>();
		this.visitados = new LinkedList<String>();
		this.listaMails = new LinkedList<String>();
		this.debug = false;
		this.depth = false;
		this.persistent = false;
		this.pozos = false;
		this.multilang = false;
		this.threads = false;
		this.prx = false;
		this.profundidad = -1;
		this.pozosFilename = null;
		this.multilangFilename = null;
		this.cantThreads = -1;
		this.proxyURL = null;
	}

	public static Estructuras getInstance() {
		if (Estructuras.e == null) {
			Estructuras.e = new Estructuras();
		}
		return Estructuras.e;
	}

	public void inicializarEstructuras(Object[] args) throws Exception {
		if (args.length > 0) {
			this.listaURLs.add((String)args[args.length]);
			for (int i = 0; i < args.length - 1; i++) {
				String entrada = (String) args[i];
				switch (entrada) {
				case "-d":
					if (!this.debug) {
						this.debug = true;
					} else {
						throw new Exception("Parametro -d repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-depth":
					if (!this.depth) {
						this.depth = true;
						i++;
						this.profundidad = Integer.parseInt((String) args[i]);
					} else {
						throw new Exception("Parametro -depth repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-persistent":
					if (!this.persistent) {
						this.persistent = true;
					} else {
						throw new Exception("Parametro -persistent repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-pozos":
					if (!this.pozos) {
						this.pozos = true;
						i++;
						this.pozosFilename = (String) args[i];
					} else {
						throw new Exception("Parametro -pozos repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-multilang":
					if (!this.multilang) {
						i++;
						this.multilang = true;
						this.multilangFilename = (String) args[i];
					} else {
						throw new Exception("Parametro -multilang repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-p":
					if (!this.threads) {
						i++;
						this.threads = true;
						this.cantThreads = Integer.parseInt((String) args[i]);
					} else {
						throw new Exception("Parametro -p repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				case "-prx":
					if (!this.prx) {
						i++;
						this.prx = true;
						this.proxyURL = (String) args[i];
					} else {
						throw new Exception("Parametro -prx repetido.\n" + Estructuras.mensajeErrorUsage);
					}
					break;
				default:
					throw new Exception("Parametro incorrecto" + Estructuras.mensajeErrorUsage);
				}
			}
			this.nivelPorURL.put((String)args[args.length], this.profundidad);
		} else {
			throw new Exception(Estructuras.mensajeErrorUsage);
		}
	}
	
	public String obtenerMensajeGET(URL url) {
		String result = "GET " + url.getPath();
		result = this.persistent? result + " HTTP/1.1" : result + " HTTP/1.0";
		
		result = result + "Host: " + url.getHost();
		result = result + "\n";
		return result;
	}
	
	public void imprimirMails(List<String> mails, String URL) {
		//FIXME evitar concurrencia
		System.out.println("Mails encontrados en la URL: " + URL);
		int i = 1;
		for (String s : mails) {
			System.out.println(i++ + " - " + s);
		}
	}
	
	public List<String> getListaURLs() {
		return listaURLs;
	}

	public void setListaURLs(List<String> listaURLs) {
		this.listaURLs = listaURLs;
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

	public boolean isThreads() {
		return threads;
	}

	public void setThreads(boolean threads) {
		this.threads = threads;
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

	public void setCantThreads(int cantThreads) {
		this.cantThreads = cantThreads;
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
}
