package ch.ethz.mergesort.threads;

import ch.ethz.mergesort.Sorter;

public abstract class SortingTask extends MergeSortTask {
	protected final int size;

	public SortingTask(int id, int place, int size) {
		super("sorting-task-", id, place);
		this.size = size;
	}

	public void run() {
		Sorter sorter = new Sorter(id, size);
		sorter.run();
		array = sorter.getArray();
	}
}
