package main;

public class Productor implements Runnable {
	
	private Procesamiento q;

	public Productor(Procesamiento q) {
		this.q = q;
		new Thread(this, "Producer").start();
	}

	public void run() {
		for (int i = 0; i<10; i++) {
			q.put(i);
		}
	}
}
