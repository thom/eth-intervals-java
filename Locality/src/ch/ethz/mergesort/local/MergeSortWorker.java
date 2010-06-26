package ch.ethz.mergesort.local;

public abstract class MergeSortWorker extends Thread {
	public MergeSortWorker(String name) {
		super(name);
	}

	public abstract int getWorkerId();

	public abstract Integer[] getArray();
}
