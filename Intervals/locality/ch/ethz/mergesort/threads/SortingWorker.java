package ch.ethz.mergesort.threads;

import ch.ethz.mergesort.Sorter;

public abstract class SortingWorker extends MergeSortWorker {
	private int size;

	public SortingWorker(int id, int size) {
		super("sorting-worker-", id);
		this.size = size;
	}

	public void run() {
		Sorter sorter = new Sorter(id, size);
		sorter.run();
		array = sorter.getArray();
	}
}
