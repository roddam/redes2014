package main;

import java.io.File;
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
	private static List<Nodo> urlsSinProcesar = new LinkedList<Nodo>();
	private static List<HilosRedbot> hilosDisponibles = new ArrayList<HilosRedbot>();
	private static boolean[] hilosEjecutando;
	
	/**
	 * Variable de control para avisar que ya se termino el procesamiento
	 */
	public static boolean seTerminoProcesamiento = false;

	/**
	 * variable auxiliar para la construccion de loops
	 */
	private List<Nodo> ciclo;

	/**
	 * Contructor
	 */
	public Estructuras() {
	}

	/**
	 * Metodo que inicializa las estructuras. En caso de producirse algun error
	 * se termina la ejecucion.
	 */
	public void inicializarEstructuras(String[] args, Estructuras e) throws IllegalArgumentException, MalformedURLException {
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
		Nodo raiz = new Nodo(0, new URL(args[args.length - 1]));
		Estructuras.urlsSinProcesar.add(raiz);
		hilosEjecutando = new boolean[cantThreads];
		for (int i = 0; i < cantThreads; i++) {
			hilosEjecutando[i] = false;
			HilosRedbot h = new HilosRedbot(i, debug, depth, persistent, pozos, multilang, prx,
					profundidad, pozosFilename, multilangFilename, proxyURL, e);
			Estructuras.hilosDisponibles.add(h);
		}
		this.ciclo = new LinkedList<Nodo>();
	}

	/**
	 * Metodo que inicia la ejecucion de los hilos de procesamiento
	 */
	public void iniciarHilosRedbot() {
		for (HilosRedbot h : hilosDisponibles) {
			hilosEjecutando[h.getIdHilo()] = true;
			h.start();
		}
	}

	/********************************************************************************************************************************
	 ******************************************* FUNCIONES MUTUO EXCLUIDAS **********************************************************
	 *******************************************************************************************************************************/

	public synchronized void agregarVisitado(String urlNUeva) {
		Estructuras.visitados.add(urlNUeva);
	}

	public synchronized boolean estaVisitado(String url) {
		return Estructuras.visitados.contains(url);
	}

	public synchronized void agregarPozo(String pozo) {
		Estructuras.setPozos.add(pozo);
	}

	public synchronized void agregarMultilenguaje(String multilenguaje) {
		Estructuras.setMultilenguaje.add(multilenguaje);
	}

	public synchronized boolean nodosSinPocesarEsVacia() {
		return urlsSinProcesar.isEmpty();

	}
	
	public synchronized void agregarLoop(String loop) {
		Estructuras.loops.add(loop);
	}

	public synchronized Nodo getUrlSinProcesar(int idHilo) {
		Nodo result = null;
		if (urlsSinProcesar.isEmpty()) {
			try {
				System.err
						.println("Me voy a dormir porque no hay URLs en la lista: "
								+ idHilo);
				hilosEjecutando[idHilo] = false;
				wait();
				System.err.println("Estaba dormido pero alguien me desperto: "
						+ idHilo);
				hilosEjecutando[idHilo] = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!urlsSinProcesar.isEmpty()) {
			result = urlsSinProcesar.remove(0);
		}
		return result;
	}

	public synchronized void setUrlsSinProcesar(Nodo n) {
		Estructuras.urlsSinProcesar.add(n);
		notify();
	}

	public synchronized List<HilosRedbot> getHilosDisponibles() {
		return hilosDisponibles;
	}

	public synchronized void setHilosDisponibles(List<HilosRedbot> hilosDisponibles) {
		Estructuras.hilosDisponibles = hilosDisponibles;
	}

	public synchronized boolean hayOtrosHilosEjecutand(int idHilo) {
		boolean result = false;
		for (int i = 0; i < hilosDisponibles.size(); i++) {
			if (i != idHilo && hilosEjecutando[i]) {
				result = true;
				break;
			}
		}
		return result;
	}

	public synchronized void dormirHilo(int idHilo) {
		try {
			// System.out.println("TERMINE DE PROCESAR Y NO HABIA MAS URLS PERO SI HILOS EJECUTANDO");
			hilosEjecutando[idHilo] = false;
			wait();
			hilosEjecutando[idHilo] = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void finalizarHilos() {
		seTerminoProcesamiento = true;
		notifyAll();
		if (pozos) {
			guardarListasArchivo(pozosFilename);
		}
		if (multilang) {
			guardarListasArchivo(multilangFilename);
		}
	}

	public synchronized void termineDeEjecutar(int idHilo) {
		hilosEjecutando[idHilo] = false;
	}

	public synchronized void guardarListasArchivo(String rutaArchivo){
		File archivo = new File(rutaArchivo);
		if (archivo.exists()) {
		}
	}
	
	public synchronized void encontrarLoops(Nodo nodoPadre, Nodo nodoVisitado, Nodo nodoActual) {
		if (!nodoActual.equals(nodoVisitado)) {
			for (Nodo n : nodoActual.getPadres()) {
				// lo agrego al ciclo
				if (!this.ciclo.contains(n)) {
					this.ciclo.add(n);
					encontrarLoops(nodoPadre, nodoVisitado, n);
					this.ciclo.remove(n);
				}
			}
		} else {
			// encontre un ciclo lo agrego a la estructura.
			String loop = "";
			for (Nodo n : this.ciclo) {
				loop = loop + " <-- " + n.toString();
			}
			loop = nodoPadre.toString() + loop + " <-- " + nodoPadre.toString(); 
			loops.add(loop);
		}
	}
}