package main;

public class ThreadRedbot extends Thread {
	
	private Nodo nodo;
	private int prof;
	
	private String nombre;
	
	public ThreadRedbot(String s){
		this.nombre = s;
	}

	public void addNodo(Nodo nodo,int prof){
		this.nodo = nodo; 
		this.prof = prof;
	}
	
	/**
	 * @see java.lang.Thread#run()
	 * 
	 */
	@Override
	public void run() {
		try {
			System.out.println("INICIO THREAD PROCESAR NODO :::::::::: " + this.nombre);
			Thread.sleep(5*1000);
			if (this.nombre.equals("Hilo 2")) {
				this.wait();
			} else {
				notify();
			}
			System.out.println("FIN THREAD PROCESAR NODO :::::::::: " + this.nombre);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Nodo getNodo() {
		return nodo;
	}

	public void setNodo(Nodo nodo) {
		this.nodo = nodo;
	}

	public int getProf() {
		return prof;
	}

	public void setProf(int prof) {
		this.prof = prof;
	}
}
