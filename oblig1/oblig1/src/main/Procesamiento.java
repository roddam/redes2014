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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Procesamiento {

	private String baseURL;
	private static int puertoDefecto = 80;

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
	private List<String> lista_URLs;
	private List<String> visitados;
	private List<String> camino;
	private List<String> listaMails;

	public Procesamiento() {
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

	/**
	 * Parsea la entrada
	 * 
	 * @throws Exception
	 */
	public void parserEntrada() throws Exception {

		InputStreamReader io = new InputStreamReader(System.in);
		BufferedReader inBuffer = new BufferedReader(io);
		String lineaEntrada = inBuffer.readLine();
		int inicio = lineaEntrada.indexOf(" ");
		int fin = lineaEntrada.lastIndexOf(" ");
		this.baseURL = lineaEntrada.substring(fin + 1);
		String comando = lineaEntrada.substring(0, inicio);
		if (comando.equals("redbot")) {
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
		} else {
			throw new Exception("Usage: redbot [-d] [-depth N]\n"
					+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
					+ "[-p threads] [-prx proxyURL] URL");
		}
	}

	/**
	 * Inicializa las estrcturas o variables dependiendo de las variables de
	 * control.
	 */
	public void inicializarRedbot() {
		this.lista_URLs = new LinkedList<String>();
		this.visitados = new LinkedList<String>();
		this.camino = new LinkedList<String>();
		this.lista_URLs.add(this.baseURL);
		this.camino.add(this.baseURL);
		this.listaMails = new LinkedList<String>();
	}

	// Ver si preferimos nombrar las variablas con algo menos asociado a grafos
	public void DFS() {
		//while(!this.lista_URLs.isEmpty()){
			String v = this.lista_URLs.remove(0); //asumo que lista_URLs es correcto pq los controles estan en el parser
			System.out.println("############## URL que estoy procesando #################");
			System.out.println(v);
			System.out.println("#########################################################");
			try {
				if (this.profundidad == 0) {
					// es pozo, por lo que vimos en el monitoreo creo que este es el caso de un pozo.
					// "Si par�s por profundidad y tenes links de salida son "
					System.out.println("############## PROFUNDIDAD es 0 #################");
				} else {  
					if (!this.visitados.contains(v)) { //ver si el casteo es necesario
						this.visitados.add(v);
						this.camino.add(v);
						
						// Preprocesamiento(v,lista_URLs,mails) 
						//en lista_URLs agrego las URLs hijas para seguir navegando, mails lo uso para imprimir en pantalla
						// Imprimir(mails) //Ver pq no respeto lo de tiempo real y sino imprimir en la funci�n anterior
						/*****************/
						URL url = new URL(v); // (this.baseURL); CREO QUE DEBERIA SER V
						//System.out.println("Host: " + url.getHost());
						//System.out.println("Puerto: " + url.getPort());
						//System.out.println("Path: " + url.getPath());
						InetAddress ia = InetAddress.getByName(url.getHost());
						System.out.println("InetAddress: " + ia.getCanonicalHostName());
						int puerto = url.getPort() == -1 ? Procesamiento.puertoDefecto : url.getPort();
						Socket sc = new Socket(ia, puerto);
						PrintWriter pw = new PrintWriter(sc.getOutputStream());
						pw.println("GET " + url.getPath() + " HTTP/1.1");
						pw.println("Host: " + url.getHost());
						pw.println();
						pw.flush();
						
						BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
						String html;
	//					href=\"http[s]?://([^\"]+)
						Pattern pat = Pattern.compile("href=\"(http[s]?://[^\"]+)\"");
						while ((html = br.readLine()) != null) {
							Matcher match = pat.matcher(html);
							while (match.find()) {
								this.lista_URLs.add(match.group(1));
							}
						}
						br.close();
						sc.close();	
						/****************/
						//System.out.println("################################ Mails encontrados ################################");
						//for (String mail : this.listaMails) {
							//System.out.println(mail);
						//}
						//System.out.println("###################################################################################");
						//System.out.println();
						System.out.println("################################ Links encontrados ################################");
						for (String links : this.lista_URLs) {
							System.out.println(links);
						}
						System.out.println("###################################################################################");
						/****************/
						
						if (!this.lista_URLs.isEmpty()) {
							this.profundidad--; 				
							while(!this.lista_URLs.isEmpty()){
								DFS();
							}
						} else {
							// si tengo hermanos pueden llegar a estar en la lista
							// es pozo???
						}
						this.camino.remove(v); //ver si el casteo es necesario
					} else {
						if (this.camino.contains(v)) {
							this.ciclos++; //ac� lo detecto y cuento pero no se si es necesario hacerlo, capaz no sea necesario camino
						}
					}
				}
			} catch (MalformedURLException e) {
				System.out.println("############################# --------> " + e.getMessage());
				e.printStackTrace();
			} catch (UnknownHostException e) {
				System.out.println("############################# --------> " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("############################# --------> " + e.getMessage());
				e.printStackTrace();
			}
		//}
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

	public static int getPuertoDefecto() {
		return Procesamiento.puertoDefecto;
	}

	public static void setPuertoDefecto(int puertoDefecto) {
		Procesamiento.puertoDefecto = puertoDefecto;
	}

	public int getCiclos() {
		return ciclos;
	}

	public void setCiclos(int ciclos) {
		this.ciclos = ciclos;
	}

	public List<String> getLista_URLs() {
		return lista_URLs;
	}

	public void setLista_URLs(List<String> lista_URLs) {
		this.lista_URLs = lista_URLs;
	}

	public List<String> getVisitados() {
		return visitados;
	}

	public void setVisitados(List<String> visitados) {
		this.visitados = visitados;
	}

	public List<String> getCamino() {
		return camino;
	}

	public void setCamino(List<String> camino) {
		this.camino = camino;
	}
}

// redbot -d -depth 5 -persistent -pozos /home/rdamiano/ejPozos -multilang
// /home/rdamiano/ejMultilang -p 5 -prx httpproxy.fing.edu.uy
// http://www.fing.edu.uy/inco/cursos/index.html
