package ch.ethz.mergesort.threads;

import ch.ethz.mergesort.Merger;

public abstract class MergingTask extends MergeSortTask {
	protected final MergeSortTask left;
	protected final MergeSortTask right;

	public MergingTask(int id, int node, MergeSortTask left,
			MergeSortTask right) {
		super("merging-task-", id, node);
		this.left = left;
		this.right = right;
	}

	public void run() {
		// Wait for left and right predecessor to finish
		try {
			left.join();
			right.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Create merger
		Merger merger = new Merger(id, left.array, right.array);

		// Initialize merged array
		merger.run();
		array = merger.getArray();
	}
}