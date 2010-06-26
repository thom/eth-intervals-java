package ch.ethz.mergesort.global;

import java.util.Arrays;

public abstract class SortingWorker extends MergeSortWorker {
	public SortingWorker(int id, int[] sharedArray, int start, int end,
			int upperBound) {
		super("sorting-worker-", id, sharedArray, start, end);
	}

	public void run() {
		// Sort array
		Arrays.sort(sharedArray, start, end);
	}
}
