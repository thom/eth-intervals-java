package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, PlaceID placeID,
			int id, int size) {
		// System.out.printf("Sorter %d: place %d\n", id, placeID.id);
		return new SortingTask(dep, placeID, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, PlaceID placeID,
			int id, MergeSortTask left, MergeSortTask right) {
		// System.out.printf("Merger %d: place %d\n", id, placeID.id);
		return new MergingTask(dep, placeID, id, left, right);
	}
}
