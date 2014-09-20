package main;

public class Redbot implements Runnable {
	private Procesamiento q;

	public Redbot(Procesamiento q) {
		this.q = q;
		new Thread(this, "Consumer").start();
	}

	public void run() {
		for (int i = 0; i<10; i++) {
			q.get();
		}
	}
}
