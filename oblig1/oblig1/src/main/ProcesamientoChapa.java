package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ProcesamientoChapa {

	private String baseURL;

	private boolean debug;
	private boolean depth;
	private int profundidad;
	private boolean persistent = false;
	private boolean pozos = false;
	private String pozosFilename;
	private boolean multilang = false;
	private String multilangFilename;
	private boolean threads = false;
	private int cantThreads;
	private boolean prx = false;
	private String proxyURL;
	private int ciclos;

	public ProcesamientoChapa() {
		super();
		this.debug = false;
		this.depth = false;
		this.profundidad = -1;
		this.persistent = false;
		this.pozos = false;
		this.pozosFilename = null;
		this.multilang = false;
		this.multilangFilename = null;
		this.threads = false;
		this.cantThreads = -1;
		this.prx = false;
		this.proxyURL = null;
		this.ciclos = 0;
	}

	public void parserEntrada() throws Exception {

		InputStreamReader io = new InputStreamReader(System.in);
		BufferedReader inBuffer = new BufferedReader(io);
		String lineaEntrada = inBuffer.readLine();
		int inicio = lineaEntrada.indexOf(" ");
		int fin = lineaEntrada.lastIndexOf(" ");
		this.baseURL = lineaEntrada.substring(fin + 1);
		System.out.println("BaseURL: " + this.baseURL);
		if (fin > inicio) {
			lineaEntrada = lineaEntrada.substring(inicio + 1, fin);
			System.out.println("Lo que va quedando: " + lineaEntrada);
			String[] parametros = lineaEntrada.split(" ");
			for (int i = 0; i < parametros.length; i++) {
				System.out.println("Parametro " + i + ": " + parametros[i]);
				String entrada = parametros[i];
				switch (entrada) {
				case "-d":
					if (this.debug) {
						throw new Exception("Parametro repetido.");
					} else {
						this.debug = true;
					}
					break;
				case "-depth":
					if (this.depth) {
						throw new Exception("Parametro repetido.");
					} else {
						this.depth = true;
						i++;
						this.profundidad = Integer.parseInt(parametros[i]);
						System.out.println("-----> Parametro " + i + ": "
								+ this.profundidad);
					}
					break;
				case "-persistent":
					if (this.persistent) {
						throw new Exception("Parametro repetido.");
					} else {
						this.persistent = true;
					}
					break;
				case "-pozos":
					if (this.pozos) {
						throw new Exception("Parametro repetido.");
					} else {
						this.pozos = true;
						i++;
						this.pozosFilename = parametros[i];
						System.out.println("-----> Parametro " + i + ": "
								+ this.pozosFilename);
					}
					break;
				case "-multilang":
					if (this.multilang) {
						throw new Exception("Parametro repetido.");
					} else {
						i++;
						this.multilang = true;
						this.multilangFilename = parametros[i];
						System.out.println("-----> Parametro " + i + ": "
								+ this.multilangFilename);
					}
					break;
				case "-p":
					if (this.threads) {
						throw new Exception("Parametro repetido.");
					} else {
						i++;
						this.threads = true;
						this.cantThreads = Integer.parseInt(parametros[i]);
						System.out.println("-----> Parametro " + i + ": "
								+ this.cantThreads);
					}
					break;
				case "-prx":
					if (this.prx) {
						throw new Exception("Parametro repetido.");
					} else {
						i++;
						this.prx = true;
						this.proxyURL = parametros[i];
						System.out.println("-----> Parametro " + i + ": "
								+ this.proxyURL);
					}
					break;
				default:
					throw new Exception("Parametro incorrecto.");
				}
			}
		}
	}

	public void inicializarRedbot() {
		List<String> lista_URLs = new LinkedList<String>();
		List<String> visitados = new LinkedList<String>();
		List<String> camino = new LinkedList<String>();
		lista_URLs.add(this.baseURL);
		camino.add(this.baseURL);
		this.DFS(lista_URLs, visitados, camino, this.profundidad);
	}

	// Ver si preferimos nombrar las variablas con algo menos asociado a grafos
	public void DFS(List<String> lista_URLs, List<String> visitados, List<String> camino, int profundidad) {
		String v = lista_URLs.remove(0); //asumo que lista_URLs es correcto pq los controles estan en el parser
		if (profundidad == 0) {
			// es pozo, por lo que vimos en el monitoreo creo que este es el caso de un pozo.
			// "Si par�s por profundidad y tenes links de salida son "
		} else {
			if (!visitados.contains(v)) { //ver si el casteo es necesario
				visitados.add(v);
				camino.add(v);
				// Preprocesamiento(v,lista_URLs,mails) 
				//en lista_URLs agrego las URLs hijas para seguir navegando, mails lo uso para imprimir en pantalla
				// Imprimir(mails) //Ver pq no respeto lo de tiempo real y sino imprimir en la funci�n anterior
				if (!lista_URLs.isEmpty()) {
					this.profundidad--; 				
					DFS(lista_URLs, visitados, camino, profundidad);
				} else {
					// si tengo hermanos pueden llegar a estar en la lista
					// es pozo???
				}
				camino.remove(v); //ver si el casteo es necesario
			} else {
				if (camino.contains(v)) {
					this.ciclos++; //ac� lo detecto y cuento pero no se si es necesario hacerlo, capaz no sea necesario camino
				}
			}
		}
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
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

	public int getProfundidad() {
		return profundidad;
	}

	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
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

	public String getPozosFilename() {
		return pozosFilename;
	}

	public void setPozosFilename(String pozosFilename) {
		this.pozosFilename = pozosFilename;
	}

	public boolean isMultilang() {
		return multilang;
	}

	public void setMultilang(boolean multilang) {
		this.multilang = multilang;
	}

	public String getMultilangFilename() {
		return multilangFilename;
	}

	public void setMultilangFilename(String multilangFilename) {
		this.multilangFilename = multilangFilename;
	}

	public boolean isThreads() {
		return threads;
	}

	public void setThreads(boolean threads) {
		this.threads = threads;
	}

	public int getCantThreads() {
		return cantThreads;
	}

	public void setCantThreads(int cantThreads) {
		this.cantThreads = cantThreads;
	}

	public boolean isPrx() {
		return prx;
	}

	public void setPrx(boolean prx) {
		this.prx = prx;
	}

	public String getProxyURL() {
		return proxyURL;
	}

	public void setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
	}

	public int getCiclos() {
		return ciclos;
	}

	public void setCiclos(int ciclos) {
		this.ciclos = ciclos;
	}
}
