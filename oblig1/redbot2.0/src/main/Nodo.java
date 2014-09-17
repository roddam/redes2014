package main;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Nodo {

	private List<String> mailsEncontrados;
	private List<Nodo> adyacentes;
	private URL url;
	private int prof;
	private List<String> camino;

	public Nodo() {
		this.adyacentes = new LinkedList<Nodo>();
		this.mailsEncontrados = new LinkedList<String>();
		this.camino = new LinkedList<String>();
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public List<Nodo> getAdyacentes() {
		return adyacentes;
	}

	public void setAdyacentes(List<Nodo> adyacentes) {
		this.adyacentes = adyacentes;
	}

	public int getProf() {
		return prof;
	}

	public void setProf(int prof) {
		this.prof = prof;
	}

	public List<String> getMailsEncontrados() {
		return mailsEncontrados;
	}

	public void setMailsEncontrados(List<String> mailsEncontrados) {
		this.mailsEncontrados = mailsEncontrados;
	}

	public List<String> getCamino() {
		return camino;
	}

	public void setCamino(List<String> camino) {
		this.camino = camino;
	}

	/**
	 * crea un Srting asociado a un nodo de la siguiente manera:
	 * URL.protocol://URL.host:URL.portURL.path
	 */
	public String toString() {
		return this.getUrl().getProtocol() + "://" + this.getUrl().getHost()
				+ ":" + this.getUrl().getPort() + this.getUrl().getPath();
	}
//	public static void main(String[] args) {
//		List<String> listaStrings = new LinkedList<String>();
//		Set<String> setStrings = new HashSet<String>();
//		
//		for (int i = 0 ; i < 6; i++) {
//			for (int j = 0; j < 6; j++) {
//				String s = "Un String con un indice: " + i;
//				listaStrings.add(s);
//				setStrings.add(s);
//			}
//		}
//		System.out.println("Cantidad de elementos en la lista: " + listaStrings.size());
//		for (String s : listaStrings) {
//			System.err.println(s);
//		}
//		System.out.println("Cantidad de elementos en el set: " + setStrings.size());
//		for (String s : setStrings) {
//			System.out.println(s);
//		}
//	}
}
