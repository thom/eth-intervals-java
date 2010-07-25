package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, PlaceID placeID, int id,
			int size) {
		// TODO: Set random place
		return new SortingTask(dep, placeID, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, PlaceID placeID, int id,
			MergeSortTask left, MergeSortTask right) {
		// TODO: Set random place
		return new MergingTask(dep, placeID, id, left, right);
	}
}