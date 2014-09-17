package main;

public class ThreadRedbot extends Thread {
	
	private Nodo nodo;
	private int prof;
	
	public ThreadRedbot(Nodo nodo, int prof) {
		this.nodo = nodo; 
		this.prof = prof;
	}

	/**
	 * @see java.lang.Thread#run()
	 * 
	 */
	@Override
	public void run() {
		Estructuras e = Estructuras.getInstance();
		e.procesarURL(this.nodo, this.prof);
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
