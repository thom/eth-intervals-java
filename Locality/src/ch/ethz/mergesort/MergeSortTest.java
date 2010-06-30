package ch.ethz.mergesort;

import java.util.ArrayList;

import ch.ethz.util.StopWatch;

public abstract class MergeSortTest {
	private int arraySize;
	private int upperBound;
	private int units;
	private ArrayList<MergingWorker> mergingWorkers;
	private SortingWorker[] sortingWorkers;

	public MergeSortTest(int arraySize, int upperBound) {
		this.arraySize = arraySize;
		this.upperBound = upperBound;
		this.units = Config.units.size();
	}

	public abstract SortingWorker createSortingWorker(int id, int size,
			int upperBound);

	public abstract MergingWorker createMergingWorker(int id,
			MergeSortWorker left, MergeSortWorker right);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		// Create workers hierarchy
		sortingWorkers = new SortingWorker[units];
		int sorterArraySize = arraySize / units;
		for (int i = 0; i < units; i++) {
			sortingWorkers[i] = createSortingWorker(i, sorterArraySize,
					upperBound);
		}
		mergingWorkers = new ArrayList<MergingWorker>();
		createMergerHierarchy(units / 2, 0, sortingWorkers);

		// Start workers
		for (SortingWorker sw : sortingWorkers) {
			sw.start();
		}
		for (MergingWorker mw : mergingWorkers) {
			mw.start();
		}

		// Wait for workers to finish
		MergingWorker lastMerger = mergingWorkers
				.get(mergingWorkers.size() - 1);
		try {
			lastMerger.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop stop watch
		stopWatch.stop();

		// Check result
		Integer[] result = lastMerger.array;
		for (int i = 0; i < result.length - 1; i++) {
			if (result[i] > result[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return stopWatch.getElapsedTime();
	}

	private void createMergerHierarchy(int number, int id,
			MergeSortWorker[] pred) {
		MergeSortWorker[] newPred = new MergeSortWorker[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				MergingWorker mw = createMergingWorker(newId, pred[2 * i],
						pred[(2 * i) + 1]);
				newPred[i] = mw;
				mergingWorkers.add(mw);
				newId += 1;
			}
			createMergerHierarchy(number / 2, newId, newPred);
		}
	}
}
