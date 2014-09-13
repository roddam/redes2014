package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Redbot {
	
	public int valor;

	public static void main(String[] args) {
		try {
			boolean soy_raiz = true;
			Estructuras e = Estructuras.getInstance();
			
			if (soy_raiz) {
				e.inicializarEstructuras(args);
			}
			
			for (String urlString : e.getListaURLs()) {
				e.getListaURLs().remove(urlString);
				e.getVisitados().add(urlString);
				
				URL urlEnProceso = new URL(urlString);
				InetAddress ia = InetAddress.getByName(urlEnProceso.getHost());
				int puerto = urlEnProceso.getPort() == -1 ? Estructuras.puertoDefecto : urlEnProceso.getPort();
				Socket sc = new Socket(ia, puerto);
				
				PrintWriter pw = new PrintWriter(sc.getOutputStream());
				System.out.println(e.obtenerMensajeGET(urlEnProceso));
				pw.println(e.obtenerMensajeGET(urlEnProceso));
				pw.flush();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
				String html;
				
				Pattern pat = Pattern.compile("<a href=\"([^ ]*)\"( .*)?/a>");
				while ((html = br.readLine()) != null) {
					if (html.contains("href")) {
						Matcher match = pat.matcher(html);
						while (match.find()) {
							System.out.println();
						}
					}
					//aca saco las URLs y los mails
//					Matcher match = pat.matcher(html);
//					while (match.find()) {
//						System.out.println(match.group(1));
//					}
				}
				
				/* aca saco los mails */
				
//				for (String url : listaMail) {
//					if (!e.getNivelPorURL().containsKey(url)) {
//						e.getNivelPorURL().put(url, e.getNivelPorURL().get(urlString) - 1);
//					}
//				}
				br.close();
				sc.close();
				
//				e.imprimirMails(mails, urlString);
				
			}
		} catch (MalformedURLException e) {
			System.out.println("No se pudo crear la URL (" + e.getMessage() + ").");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
