package ch.ethz.mergesort;

public abstract class MergeSortWorker extends Thread {
	protected final int[] sharedArray;
	protected final int id, start, end;

	public MergeSortWorker(String name, int id, int[] sharedArray, int start,
			int end) {
		super(name + id);
		this.id = id;
		this.sharedArray = sharedArray;
		this.start = start;
		this.end = end;
	}
}
