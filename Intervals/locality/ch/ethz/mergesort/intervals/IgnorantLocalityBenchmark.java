package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, PlaceID placeID, int id,
			int size) {
		return new SortingTask(dep, null, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, PlaceID placeID, int id,
			MergeSortTask left, MergeSortTask right) {
		return new MergingTask(dep, null, id, left, right);
	}
}
