package main;

import java.util.LinkedList;
import java.util.List;

public class Nodo {

	private String nombre;
	private List<Nodo> padres;
	private List<Nodo> adyacentes;

	public Nodo(String nombre) {
		this.adyacentes = new LinkedList<Nodo>();
		this.padres = new LinkedList<Nodo>();
		this.nombre = nombre;
	}

	public List<Nodo> getAdyacentes() {
		return adyacentes;
	}

	public void setAdyacentes(List<Nodo> adyacentes) {
		this.adyacentes = adyacentes;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Nodo> getPadres() {
		return padres;
	}

	public void setPadres(List<Nodo> padres) {
		this.padres = padres;
	}

	/**
	 * Verifica si dos nodos son iguales, comparando las url asociadas.
	 */
	public boolean equals(Nodo n) {
		return this.nombre.equals(n.getNombre());
	}

	/**
	 * main para pruebas
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Nodo n0 = new Nodo("nodo0");
		Nodo n1 = new Nodo("nodo1");
		Nodo n2 = new Nodo("nodo2");
		Nodo n3 = new Nodo("nodo3");
		Nodo n4 = new Nodo("nodo4");
		Nodo n5 = new Nodo("nodo5");
		
		n0.getPadres().add(n4);
		n1.getPadres().add(n3);
		n2.getPadres().add(n0);
		n2.getPadres().add(n1);
		n3.getPadres().add(n2);
		n3.getPadres().add(n4);
		n4.getPadres().add(n1);
		n4.getPadres().add(n5);
		n5.getPadres().add(n1);
		n5.getPadres().add(n0);
		
		n0.getAdyacentes().add(n2);
		n0.getAdyacentes().add(n5);
		n1.getAdyacentes().add(n2);
		n1.getAdyacentes().add(n4);
		n1.getAdyacentes().add(n5);
		n2.getAdyacentes().add(n3);
		n3.getAdyacentes().add(n1);
		n4.getAdyacentes().add(n0);
		n4.getAdyacentes().add(n3);
		n5.getAdyacentes().add(n4);
		
		Estructuras e = new Estructuras();
		e.encontrarLoops(n3, n1, n3);
	}
}
