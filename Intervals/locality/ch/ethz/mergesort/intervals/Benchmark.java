package ch.ethz.mergesort.intervals;

import java.util.ArrayList;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;
import ch.ethz.mergesort.Main;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int sortersPerNode;
	private int numberOfSorters;
	private ArrayList<MergingTask> mergingTasks;
	private SortingTask[] sortingTasks;

	public Benchmark() {
		this.sortersPerNode = Main.sortersPerNode;
		this.numberOfSorters = Main.units.nodesSize() * sortersPerNode;
	}

	public abstract SortingTask createSortingTask(Dependency dep, Place place,
			int id, int size);

	public abstract MergingTask createMergingTask(Dependency dep, Place place,
			int id, MergeSortTask left, MergeSortTask right);

	public long run() {
		startBenchmark();

		// Create tasks hierarchy
		sortingTasks = new SortingTask[numberOfSorters];
		mergingTasks = new ArrayList<MergingTask>();
		final int sorterArraySize = Main.arraySize / numberOfSorters;

		// Create intervals
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				for (int i = 0; i < numberOfSorters; i++) {
					// TODO: Set correct place
					sortingTasks[i] = createSortingTask(subinterval, null, i,
							sorterArraySize);
				}

				createMergerHierarchy(subinterval, numberOfSorters / 2, 0,
						sortingTasks);
			}
		});

		long time = stopBenchmark();

		// Check result
		Integer[] result = mergingTasks.get(mergingTasks.size() - 1).array;
		for (int i = 0; i < result.length - 1; i++) {
			if (result[i] > result[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return time;
	}

	private void createMergerHierarchy(Dependency dep, int number, int id,
			MergeSortTask[] pred) {
		MergeSortTask[] newPred = new MergeSortTask[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				MergeSortTask left = pred[2 * i];
				MergeSortTask right = pred[(2 * i) + 1];
				MergingTask merger = createMergingTask(dep, left.place, newId,
						left, right);
				newPred[i] = merger;
				mergingTasks.add(merger);
				newId += 1;

				// Set happens-before relationships
				Intervals.addHb(left, merger);
				Intervals.addHb(right, merger);
			}
			createMergerHierarchy(dep, number / 2, newId, newPred);
		}
	}
}
