package main;


public class Principal {

	/**
	 * Mensaje de error general.
	 */
	private static String mensajeErrorUsage = "Usage: redbot [-d] [-depth N]\n"
			+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
			+ "[-p threads] [-prx proxyURL] URL";

	public static void main(String[] args) {
		try {
			Estructuras e = new Estructuras();
			e.inicializarEstructuras(args, e);
			e.iniciarHilosRedbot();
		} catch (Exception e) {
			System.err.println(mensajeErrorUsage);
		}
	}
}