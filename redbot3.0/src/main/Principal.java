package main;

import java.net.MalformedURLException;

public class Principal {

	/**
	 * Mensaje de error general.
	 */
	private static String mensajeErrorUsage = "Usage: redbot [-d] [-depth N]\n"
			+ "[-persistent] [-pozos fileName] [-multilang fileName]\n"
			+ "[-p threads] [-prx proxyURL] URL";
	
	public static void main(String[] args) {
		try {
			Estructuras e = Estructuras.getInstance();
			e.inicializarEstructuras(args);
			e.iniciarHilosRedbot();
		} catch(IllegalArgumentException e) {
			System.err.println("################################### " + e.getMessage() + mensajeErrorUsage);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err.println("################################### " + e.getMessage() + mensajeErrorUsage);
			e.printStackTrace();
	    } catch(Exception e) {
			System.err.println("################################### " + e.getMessage() + mensajeErrorUsage);
			e.printStackTrace();
		}
	}
}

/*
private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
private static final String HTML_MAILTO_PATTERN = "mailto:([^?]+)";

private Pattern patternTag;
private Pattern patternLink;
private Pattern patternMailTo;
private Matcher matcherTag;
private Matcher matcherLink;
private Matcher matcherMailTo;
*/