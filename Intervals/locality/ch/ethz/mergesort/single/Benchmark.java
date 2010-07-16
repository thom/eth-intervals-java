package ch.ethz.mergesort.single;

import java.util.ArrayList;

import ch.ethz.mergesort.Main;
import ch.ethz.mergesort.MergeSorter;
import ch.ethz.mergesort.Merger;
import ch.ethz.mergesort.Sorter;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	private int numberOfSorters;
	private ArrayList<Merger> mergers;
	private Sorter[] sorters;

	public Benchmark() {
		this.numberOfSorters = Main.units.nodesSize() * Main.sortersPerNode;
	}

	public long run() {
		startBenchmark();

		sorters = new Sorter[numberOfSorters];
		int sorterArraySize = Main.arraySize / numberOfSorters;
		for (int i = 0; i < numberOfSorters; i++) {
			sorters[i] = new Sorter(i, sorterArraySize);
			sorters[i].run();
		}
		mergers = new ArrayList<Merger>();
		merge(numberOfSorters / 2, 0, sorters);
		Merger lastMerger = mergers.get(mergers.size() - 1);

		long time = stopBenchmark();

		// Check result
		Integer[] result = lastMerger.getArray();
		for (int i = 0; i < result.length - 1; i++) {
			if (result[i] > result[i + 1]) {
				System.out.println("Array is not sorted!!!");
				System.exit(1);
			}
		}

		return time;
	}

	private void merge(int number, int id, MergeSorter[] pred) {
		MergeSorter[] newPred = new MergeSorter[number];
		int newId = id;
		if (!(number == 0)) {
			for (int i = 0; i < number; i++) {
				Merger merger = new Merger(newId, pred[2 * i].getArray(),
						pred[(2 * i) + 1].getArray());
				merger.run();
				newPred[i] = merger;
				mergers.add(merger);
				newId += 1;
			}
			merge(number / 2, newId, newPred);
		}
	}
}
