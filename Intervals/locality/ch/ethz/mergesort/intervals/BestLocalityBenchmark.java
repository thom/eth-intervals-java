package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, Place place, int id,
			int size) {
		return new SortingTask(dep, place, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, Place place, int id,
			MergeSortTask left, MergeSortTask right) {
		return new MergingTask(dep, place, id, left, right);
	}
}
