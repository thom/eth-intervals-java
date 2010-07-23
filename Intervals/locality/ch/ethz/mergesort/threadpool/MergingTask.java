package ch.ethz.mergesort.threadpool;

import java.util.concurrent.Future;

import ch.ethz.mergesort.Merger;

public class MergingTask extends MergeSortTask {
	protected final Future<Integer[]> left;
	protected final Future<Integer[]> right;

	public MergingTask(int id, Future<Integer[]> left, Future<Integer[]> right) {
		super("merging-task-", id);
		this.left = left;
		this.right = right;
	}

	public Integer[] call() {
		// Create merger
		Merger merger = null;
		try {
			merger = new Merger(id, left.get(), right.get());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Initialize merged array
		merger.run();
		return merger.getArray();
	}
}