package main;

public class Main {
	
	public static void main(String args[]) {
		Procesamiento q = new Procesamiento();
		new Productor(q);
		new Consumidor(q);
	}
}
