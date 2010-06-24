package ch.ethz.mergesort;

public abstract class MergeSortWorker extends Thread {
	public MergeSortWorker(String name) {
		super(name);
	}

	public abstract int getWorkerId();

	public abstract int[] getArray();
}
