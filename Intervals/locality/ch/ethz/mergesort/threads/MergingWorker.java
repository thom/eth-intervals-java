package ch.ethz.mergesort.threads;

import ch.ethz.mergesort.Merger;

public abstract class MergingWorker extends MergeSortWorker {
	protected final MergeSortWorker left;
	protected final MergeSortWorker right;

	public MergingWorker(int id, MergeSortWorker left, MergeSortWorker right) {
		super("merging-worker-", id);
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
		array = merger.run();
	}
}