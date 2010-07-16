package ch.ethz.mergesort.threads;

public abstract class MergeSortWorker extends Thread {
	protected final int id;
	protected final int node;
	protected Integer[] array;

	public MergeSortWorker(String name, int id, int node) {
		super(name + id);
		this.id = id;
		this.node = node;
	}
}
