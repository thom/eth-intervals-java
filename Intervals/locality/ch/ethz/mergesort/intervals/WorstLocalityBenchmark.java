package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.mergesort.Main;

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
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
		// System.out.printf("Merger %d: place %d, switch to %d\n", id,
		// placeID.id, Main.places.getPlaceID(placeID.id + 1).id);
		return new MergingTask(dep, Main.places.getPlaceID(placeID.id + 1), id,
				left, right);
	}
}