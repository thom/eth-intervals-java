package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class BestLocalitySortingTask extends SortingTask {
	public BestLocalitySortingTask(int id, int place, int size) {
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

class BestLocalityMergingTask extends MergingTask {
	public BestLocalityMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		super(id, place, left, right);
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

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(int id, int place, int size) {
		return new BestLocalitySortingTask(id, place, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		return new BestLocalityMergingTask(id, place, left, right);
	}
}
