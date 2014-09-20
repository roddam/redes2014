package main;

public class Procesamiento {
	
	int n;
	boolean valueSet = false;

	synchronized int get() {
		if (!valueSet)
			try {
				System.out.println("Me duermo: GET " + n);
				wait();
				System.out.println("Me despertaron: GET " + n);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		System.out.println("Got: " + n);
		valueSet = false;
		notify();
		return n;
	}

	synchronized void put(int n) {
		if (valueSet)
			try {
				System.out.println("Me duermo: PUT " + n);
				wait();
				System.out.println("Me despertaron: PUT " + n);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		this.n = n;
		valueSet = true;
		System.out.println("Put: " + n);
		notify();
	}
}