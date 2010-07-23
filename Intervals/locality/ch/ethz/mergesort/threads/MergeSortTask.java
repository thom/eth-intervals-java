package ch.ethz.mergesort.threads;

public abstract class MergeSortTask extends Thread {
	protected final int id;
	protected final int node;
	protected Integer[] array;

	public MergeSortTask(String name, int id, int node) {
		super(name + id);
		this.id = id;
		this.node = node;
	}
}
