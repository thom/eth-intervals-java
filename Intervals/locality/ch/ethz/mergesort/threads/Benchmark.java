package ch.ethz.mergesort.threads;

import java.util.ArrayList;

import ch.ethz.mergesort.Main;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int sortersPerNode;
	private int numberOfSorters;
	private ArrayList<MergingWorker> mergingWorkers;
	private SortingWorker[] sortingWorkers;

	public Benchmark() {
		this.sortersPerNode = Main.sortersPerNode;
		this.numberOfSorters = Main.units.nodesSize() * sortersPerNode;
	}

	public abstract SortingWorker createSortingWorker(int id, int unit, int size);

	public abstract MergingWorker createMergingWorker(int id, int unit,
			MergeSortWorker left, MergeSortWorker right);

	public long run() {
		startBenchmark();

		// Create workers hierarchy
		sortingWorkers = new SortingWorker[numberOfSorters];
		int sorterArraySize = Main.arraySize / numberOfSorters;
		for (int i = 0; i < numberOfSorters; i++) {
			sortingWorkers[i] = createSortingWorker(i, i / sortersPerNode,
					sorterArraySize);
		}
		mergingWorkers = new ArrayList<MergingWorker>();
		createMergerHierarchy(numberOfSorters / 2, 0, sortingWorkers);

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

		long time = stopBenchmark();

		// Check result
		Integer[] result = lastMerger.array;
		for (int i = 0; i < result.length - 1; i++) {
			if (result[i] > result[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return time;
	}

	private void createMergerHierarchy(int number, int id,
			MergeSortWorker[] pred) {
		MergeSortWorker[] newPred = new MergeSortWorker[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				MergingWorker mw = createMergingWorker(newId, pred[2 * i].node,
						pred[2 * i], pred[(2 * i) + 1]);
				newPred[i] = mw;
				mergingWorkers.add(mw);
				newId += 1;
			}
			createMergerHierarchy(number / 2, newId, newPred);
		}
	}
}
