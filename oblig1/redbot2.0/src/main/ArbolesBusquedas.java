package main;

public class ArbolesBusquedas {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NodoArbol raiz = new NodoArbol("URL_inicial",true);
		
		
		//abro socket
		
		// primer nivel
		NodoArbol nivel1hijo0 = new NodoArbol("URL_H_1",false);
		NodoArbol nivel1hijo1 = new NodoArbol("URL_H_2",false);
		NodoArbol nivel1hijo2 = new NodoArbol("URL_H_3",false);
		
		
		raiz.addHijo(nivel1hijo0);
		raiz.addHijo(nivel1hijo1);
		raiz.addHijo(nivel1hijo2);
		///////////////////////////
		
		
		//hijo get URL
		//procesar URL 
		//agregar hijos al hijo.
		
		NodoArbol aux  = raiz;
		while(aux.getHijos().iterator().hasNext()){
			NodoArbol hijo = aux.getHijos().get(0);
			//recuersión (hijo)
		
		}
		
		
	}

}
