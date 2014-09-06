package main;

public class Redbot {

	public void metodoPrueba() {

	}

	public static void main(String[] args) {
		Procesamiento procesamiento = new Procesamiento();
		try {
			procesamiento.parserEntrada();
			procesamiento.inicializarRedbot();
		} catch (NumberFormatException e) {
			System.out.println("########## SE ROMPIO #############");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * Procedimiento DFS(visitados, camino, profundidad) Comienzo v =
	 * lista_URLs.primero Si profundidad == 0 //es pozo, por lo que vimos en el
	 * monitoreo creo que este es el caso de un pozo. //
	 * "Si parás por profundidad y tenes links de salida son " Else Si
	 * !visitados.pertenece(v) visitados.agregar(v) camino.agregar(v)
	 * Preprocesamiento(v,lista_URLs,mails) //en lista_URLs agrego las URLs
	 * hijas para seguir navegando, mails lo uso para imprimir en pantalla
	 * Imprimir(mails) //Ver pq no respeto lo de tiempo real Si
	 * !lista_URLs.vacio() profundidad-- DFS(visitados,camino, profundidad) Else
	 * //es pozo??? Fin camino.sacar(v) Else Si camino.pertenece(v) ciclos++ Fin
	 * Fin Fin Fin
	 * 
	 * Procedimiento Recorrido_DFS(G: grafo) {G = (V,A)} Var v: vértice Comienzo
	 * //Lo único es meter la URL original en la lista base Fin
	 */

}
