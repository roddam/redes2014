package main;

public class Consumidor implements Runnable {
	private Procesamiento q;

	public Consumidor(Procesamiento q) {
		this.q = q;
		new Thread(this, "Consumer").start();
	}

	public void run() {
		for (int i = 0; i<10; i++) {
			q.get();
		}
	}
}
