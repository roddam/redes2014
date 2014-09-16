package main;

public class Redbot {

	public static void main(String[] args) {
		try {
			Estructuras struct = Estructuras.getInstance();
			struct.inicializarEstructuras(args);	
			struct.procesarURL(struct.getRaiz(), 0);
			System.out.println("TERMINE!!!!!!!!!!!!!!");
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}	
}
