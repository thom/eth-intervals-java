package ch.ethz.mergesort;

import java.util.ArrayList;
import java.util.Random;

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

	public abstract SortingWorker createSortingWorker(int id,
			int[] sharedArray, int start, int stop, int upperBound);

	public abstract MergingWorker createMergingWorker(int id,
			int[] sharedArray, MergeSortWorker left, MergeSortWorker right);

	public long run() {
		// Initialization
		Random random = new Random();
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		// Shared array
		int[] sharedArray = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			sharedArray[i] = random.nextInt(upperBound);
		}

		// Create workers hierarchy
		sortingWorkers = new SortingWorker[units];
		int arraySliceSize = arraySize / units;
		int start, stop;
		for (int i = 0; i < units; i++) {
			start = i * arraySliceSize;
			if (i == (units - 1)) {
				stop = sharedArray.length;
			} else {
				stop = (i + 1) * arraySliceSize;
			}

			sortingWorkers[i] = createSortingWorker(i, sharedArray, start,
					stop, upperBound);
		}
		mergingWorkers = new ArrayList<MergingWorker>();
		createMergerHierarchy(units / 2, 0, sortingWorkers, sharedArray);

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
		for (int i = 0; i < sharedArray.length - 1; i++) {
			if (sharedArray[i] > sharedArray[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return stopWatch.getElapsedTime();
	}

	private void createMergerHierarchy(int number, int id,
			MergeSortWorker[] pred, int[] sharedArray) {
		MergeSortWorker[] newPred = new MergeSortWorker[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				MergingWorker mw = createMergingWorker(newId, sharedArray,
						pred[2 * i], pred[(2 * i) + 1]);
				newPred[i] = mw;
				mergingWorkers.add(mw);
				newId += 1;
			}
			createMergerHierarchy(number / 2, newId, newPred, sharedArray);
		}
	}
}
