package ch.ethz.mergesort.threads;

import java.util.ArrayList;

import ch.ethz.mergesort.Main;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int sortersPerPlace;
	private int numberOfSorters;
	private ArrayList<MergingTask> mergingTasks;
	private SortingTask[] sortingTasks;

	public Benchmark() {
		this.sortersPerPlace = Main.sortersPerPlace;
		this.numberOfSorters = Main.places.length * sortersPerPlace;
	}

	public abstract SortingTask createSortingTask(int id, int unit, int size);

	public abstract MergingTask createMergingTask(int id, int unit,
			MergeSortTask left, MergeSortTask right);

	public long run() {
		startBenchmark();

		// Create tasks hierarchy
		sortingTasks = new SortingTask[numberOfSorters];
		int sorterArraySize = Main.arraySize / numberOfSorters;
		for (int i = 0; i < numberOfSorters; i++) {
			sortingTasks[i] = createSortingTask(i, i / sortersPerPlace,
					sorterArraySize);
		}
		mergingTasks = new ArrayList<MergingTask>();
		createMergerHierarchy(numberOfSorters / 2, 0, sortingTasks);

		// Start tasks
		for (SortingTask sw : sortingTasks) {
			sw.start();
		}
		for (MergingTask mw : mergingTasks) {
			mw.start();
		}

		// Wait for tasks to finish
		MergingTask lastMerger = mergingTasks.get(mergingTasks.size() - 1);
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

	private void createMergerHierarchy(int number, int id, MergeSortTask[] pred) {
		MergeSortTask[] newPred = new MergeSortTask[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				MergingTask mw = createMergingTask(newId, pred[2 * i].place,
						pred[2 * i], pred[(2 * i) + 1]);
				newPred[i] = mw;
				mergingTasks.add(mw);
				newId += 1;
			}
			createMergerHierarchy(number / 2, newId, newPred);
		}
	}
}
