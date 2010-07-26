package ch.ethz.mergesort.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.ethz.mergesort.Main;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	protected ExecutorService exec;
	private int sortersPerPlace;
	private int numberOfSorters;
	private ArrayList<Future<Integer[]>> mergingTasks;

	public Benchmark() {
		this.sortersPerPlace = Main.sortersPerPlace;
		this.numberOfSorters = Main.places.length * sortersPerPlace;
	}

	public SortingTask createSortingTask(int id, int size) {
		return new SortingTask(id, size);
	}

	public MergingTask createMergingTask(int id, Future<Integer[]> left,
			Future<Integer[]> right) {
		return new MergingTask(id, left, right);
	}

	public long run() {
		startBenchmark();
		exec = Executors.newFixedThreadPool(Main.threads);

		// Create tasks hierarchy
		@SuppressWarnings("unchecked")
		Future<Integer[]>[] sortingTasks = (Future<Integer[]>[]) new Future<?>[numberOfSorters];
		int sorterArraySize = Main.arraySize / numberOfSorters;
		for (int i = 0; i < numberOfSorters; i++) {
			sortingTasks[i] = exec
					.submit(createSortingTask(i, sorterArraySize));
		}
		mergingTasks = new ArrayList<Future<Integer[]>>();
		createMergerHierarchy(numberOfSorters / 2, 0, sortingTasks);

		// Wait for tasks to finish
		Integer[] result = null;
		try {
			result = mergingTasks.get(mergingTasks.size() - 1).get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		exec.shutdown();
		long time = stopBenchmark();

		// Check result
		for (int i = 0; i < result.length - 1; i++) {
			if (result[i] > result[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return time;
	}

	private void createMergerHierarchy(int number, int id,
			Future<Integer[]>[] pred) {
		@SuppressWarnings("unchecked")
		Future<Integer[]>[] newPred = (Future<Integer[]>[]) new Future<?>[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				Future<Integer[]> mw = exec.submit(createMergingTask(newId,
						pred[2 * i], pred[(2 * i) + 1]));
				newPred[i] = mw;
				mergingTasks.add(mw);
				newId += 1;
			}
			createMergerHierarchy(number / 2, newId, newPred);
		}
	}
}
