package ch.ethz.mergesort;

import java.util.Arrays;
import java.util.Random;

public abstract class SortingWorker extends MergeSortWorker {
	private int size;
	private int upperBound;

	public SortingWorker(int id, int size, int upperBound) {
		super("sorting-worker-", id);
		this.size = size;
		this.upperBound = upperBound;
	}

	public void run() {
		// Initialization
		Random random = new Random();
		array = new Integer[size];

		// Fill array
		for (int i = 0; i < size; i++) {
			array[i] = random.nextInt(upperBound);
		}

		// Sort array
		Arrays.sort(array);
	}
}
