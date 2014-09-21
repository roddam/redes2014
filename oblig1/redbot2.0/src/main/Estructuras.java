package main;

import java.util.LinkedList;
import java.util.List;

public class Estructuras {
	
	public Estructuras() {
		this.ciclo = new LinkedList<Nodo>();
	}
	
	private List<Nodo> ciclo;
	
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
				loop = loop + " <-- " + n.getNombre();
			}
			loop = nodoPadre.getNombre() + loop + " <-- " + nodoPadre.getNombre(); 
			System.out.println(loop);
		}
	}
}