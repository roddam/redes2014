package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

public class HilosRedbot extends Thread {

	public static final int puertoDefecto = 80;

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
		// FIXME arreglar el mensaje
		String result = "GET ";
		// result = url.getPath().equals("") ? result + "/" : result +
		// url.getPath();
		// result = result + " HTTP/1.1\n";
		// result = result + "Connection: keep-alive\n";
		// result = result + "Host: " + url.getHost();
		// result = result + "\n";
		// result = result + "Accept: text/html\n";
		return result;
	}

	/**
	 * Metodo para procesar solo el cabezal.
	 */
	public void procesarCabezal(BufferedReader br) throws IOException {
		String html = br.readLine();
		while (!html.isEmpty()) {
			if (html.contains("HTTP/")) {
				// String[] values = html.split(" ");
				// hashCabezal.put("CODE_ERROR",values[1].trim());
				// hashCabezal.put("CODE_MSG",values[2].trim());
			} else if (html.startsWith("Content-Type")) {
				// String[] values = html.split(":");
				// hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("Content-Language")) {
				// String[] values = html.split(":");
				// hashCabezal.put(values[0].trim(), values[1].trim());
			} else if (html.startsWith("ETag")) {
				// String[] values = html.split(":");
				// hashCabezal.put(values[0].trim(),
				// values[1].trim().replace("\"", ""));
			}
			html = br.readLine();
		}
	}

	/**
	 * Genero una URL a partir del string encontrado durante el procesamiento
	 */
	public URL stringToURL(String string) {
		URL urlGenerada = null;

		return urlGenerada;
	}

	/**
	 * Metodo que realiza todo el procesamiento de una URL
	 */
	@Override
	public void run() {
		// Obtengo una URL para procesar
		boolean faltaProcesar = true;
		while (faltaProcesar) {
			String urlSinProcesar = Estructuras.getUrlSinProcesar();
			if (urlSinProcesar != null) {
				// Si hay una URL disponible, la proceso.
				// Actualizo los pozos, los loops, los visitados, los
				// multilenguaje
				// Imprimo los mails que vaya encontrando
				// Agrego a la lista de URLs sin procesar las URL que vaya
				// encontrando
				// Despierto a los hilos que pudiesen estar esperando que yo
				// termine de procesar.
				// FIXME Procesar URL.

			}
			// Despues de procesar me fijo si no hay ninguna URL para procesar y
			// si hay algun hilo ejecutando que no sea yo mismo
			if (Estructuras.getUrlSinProcesar().isEmpty()
					&& Estructuras.hayHilosProcesando(this.idHilo)) {
				// Si no hay ninguna URL para procesar y hay algun hilo
				// ejecutando, podrian aparecer mas URLs a procesar.
				// Entonces me duermo, esperando que alguno de los hilos que
				// estan procesando me despierte.
				// FIXME me tengo que dormir
			} else {
				// Si no hay ninguna URL para procesar y no hay hilos
				// procesando,
				// entonces soy el ultimo que quedo procesando y no genere
				// ninguna URL nueva.
				// Entonces fin procesamiento del hilo.
				faltaProcesar = false;
			}
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
}
