package ch.ethz.cacheefficient;

public abstract class MergeSortWorker extends Thread {
	protected final int id;
	protected Integer[] array;

	public MergeSortWorker(String name, int id) {
		super(name + id);
		this.id = id;
	}
}
