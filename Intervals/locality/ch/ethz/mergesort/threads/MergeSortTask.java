package ch.ethz.mergesort.threads;

public abstract class MergeSortTask extends Thread {
	protected final int id;
	protected final int place;
	protected Integer[] array;

	public MergeSortTask(String name, int id, int place) {
		super(name + id);
		this.id = id;
		this.place = place;
	}
}
