package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, Place place, int id,
			int size) {
		return new SortingTask(dep, null, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, Place place, int id,
			MergeSortTask left, MergeSortTask right) {
		return new MergingTask(dep, null, id, left, right);
	}
}
