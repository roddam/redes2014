package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class Redbot {
	
	public int valor;

	public static void main(Object[] args) {
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
				pw.println(e.obtenerMensajeGET(urlEnProceso));
				pw.flush();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
				String html;
				
				while ((html = br.readLine()) != null) {
					
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
