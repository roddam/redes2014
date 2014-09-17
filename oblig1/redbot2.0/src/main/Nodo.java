package main;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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

	/**
	 * Verifica si dos nodos son iguales, comparando las url asociadas.
	 */
	public boolean equals(Nodo n) {
		return this.toString().equals(n.toString());
	}

	// /**
	// * main para pruebas
	// * @param args
	// */
	// public static void main(String[] args) {
	// }
}
