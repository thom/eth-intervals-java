package ch.ethz.mergesort.threadpool;

import ch.ethz.mergesort.Sorter;

public class SortingTask extends MergeSortTask {
	protected final int size;

	public SortingTask(int id, int size) {
		super("sorting-task-", id);
		this.size = size;
	}

	public Integer[] call() {
		Sorter sorter = new Sorter(id, size);
		sorter.run();
		return sorter.getArray();
	}
}
