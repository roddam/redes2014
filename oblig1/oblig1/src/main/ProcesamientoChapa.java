package main;

public class ProcesamientoChapa implements Runnable {
	
	private Procesamiento q;

	public ProcesamientoChapa(Procesamiento q) {
		this.q = q;
		new Thread(this, "Producer").start();
	}

	public void run() {
		for (int i = 0; i<10; i++) {
			q.put(i);
		}
	}
}
