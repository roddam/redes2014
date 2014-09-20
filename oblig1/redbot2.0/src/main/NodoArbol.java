package main;

import java.util.ArrayList;
import java.util.List;

public class NodoArbol {
	
	private String elemento;
	private List<NodoArbol> hijos;
	private boolean visitado = false;
	
	public NodoArbol(String elemento, boolean visitado) {
		this.elemento = elemento;
		hijos = new ArrayList<NodoArbol>();
		this.visitado = visitado;
	}
			 
	public String getElemento() {
		return elemento;
	}
 
	public void addHijo(NodoArbol hijo) {
		hijos.add(hijo);
	}
 
	public List<NodoArbol> getHijos() {
		return hijos;
	}
 
	public boolean esNodoHoja() {
		return hijos.size() > 0;
	}
	
	

}
