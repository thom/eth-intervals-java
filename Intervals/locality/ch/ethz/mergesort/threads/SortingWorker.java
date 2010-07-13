package ch.ethz.mergesort.threads;

import java.util.Arrays;
import java.util.Random;

import ch.ethz.mergesort.Main;

public abstract class SortingWorker extends MergeSortWorker {
	private int size;

	public SortingWorker(int id, int size) {
		super("sorting-worker-", id);
		this.size = size;
	}

	public void run() {
		// Initialization
		Random random = new Random();
		array = new Integer[size];

		// Fill array
		for (int i = 0; i < size; i++) {
			array[i] = random.nextInt(Main.upperBound);
		}

		// Sort array
		Arrays.sort(array);
	}
}
