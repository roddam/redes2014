package main;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Nodo {
	
	private int nivel;
	private URL url;
	private List<Nodo> adyacentes;
	private Set<String> listaMails;
	
	public Nodo(int nivel, URL url) {
		super();
		this.nivel = nivel;
		this.adyacentes = new LinkedList<Nodo>();
		this.url = url;
		this.listaMails = new HashSet<String>();
	}
	
	public int getNivel() {
		return nivel;
	}
	
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
	
	public List<Nodo> getAdyacentes() {
		return adyacentes;
	}
	
	public void setAdyacentes(List<Nodo> adyacentes) {
		this.adyacentes = adyacentes;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Set<String> getListaMails() {
		return listaMails;
	}

	public void setListaMails(Set<String> listaMails) {
		this.listaMails = listaMails;
	}
	
	@Override
	public String toString() {
		return this.getUrl().getProtocol() + "://" + this.getUrl().getHost()
				+ ":" + this.getUrl().getPort() + this.getUrl().getPath();
	}
	
	public boolean equals(Nodo n) {
		return this.toString().equals(n.toString());
	}
}
