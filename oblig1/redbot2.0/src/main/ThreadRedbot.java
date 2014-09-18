package main;

public class ThreadRedbot extends Thread {
	
	private Nodo nodo;
	private int prof;
	
	public ThreadRedbot(){
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
		System.out.println("INICIO THREAD PROCESAR NODO :::::::::: "+this.nodo.getUrl());
		Estructuras e = Estructuras.getInstance();
		e.procesarURL(this.nodo, this.prof,true);
		System.out.println("FIND THREAD PROCESAR NODO :::::::::: "+this.nodo.getUrl());
		Estructuras.cantThreads++;
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
