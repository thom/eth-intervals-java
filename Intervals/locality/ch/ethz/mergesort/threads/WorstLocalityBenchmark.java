package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class WorstLocalitySortingTask extends SortingTask {
	public WorstLocalitySortingTask(int id, int place, int size) {
		super(id, place, size);
	}

	public void run() {
		try {
			Affinity.set(Main.places.get(place));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class WorstLocalityMergingTask extends MergingTask {
	public WorstLocalityMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		super(id, place, left, right);
	}

	public void run() {
		try {
			Affinity.set(Main.places.get((place + 1) % Main.places.length));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(int id, int place, int size) {
		return new WorstLocalitySortingTask(id, place, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		return new WorstLocalityMergingTask(id, place, left, right);
	}
}