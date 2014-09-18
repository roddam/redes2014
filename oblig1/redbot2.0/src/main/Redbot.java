package main;

import java.net.MalformedURLException;

public class Redbot {

	public static void main(String[] args) {
		try {
			Estructuras struct = Estructuras.getInstance();
			struct.inicializarEstructuras(args);	
			struct.procesarURL(struct.getRaiz(), 0,false);
			System.out.println("TERMINE!!!!!!!!!!!!!!");
		} catch (MalformedURLException e) {
			System.out.println("La URL ingresada no es valida.\nPor favor, verifique su correctitud.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}	
}
