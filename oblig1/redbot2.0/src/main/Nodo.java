package main;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Nodo {
	
	private List<String> mailsEncontrados;
	private List<Nodo> adyacentes;
	private URL url;
	private int prof;
	
	
	public Nodo() {
		this.adyacentes = new LinkedList<Nodo>();
		this.mailsEncontrados = new LinkedList<String>();
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
	
	public String toString() {
		return this.getUrl().getProtocol() + "://" + this.getUrl().getHost() + ":" + this.getUrl().getPort() + this.getUrl().getPath();
	}
}
