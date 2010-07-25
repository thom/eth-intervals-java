package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, PlaceID placeID, int id,
			int size) {
		// TODO: Set worst case place
		return new SortingTask(dep, placeID, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, PlaceID placeID, int id,
			MergeSortTask left, MergeSortTask right) {
		// TODO: Set worst case place
		return new MergingTask(dep, placeID, id, left, right);
	}
}