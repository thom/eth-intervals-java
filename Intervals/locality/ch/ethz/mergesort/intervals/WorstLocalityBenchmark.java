package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, Place place, int id,
			int size) {
		// TODO: Set worst case place
		return new SortingTask(dep, place, id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, Place place, int id,
			MergeSortTask left, MergeSortTask right) {
		// TODO: Set worst case place
		return new MergingTask(dep, place, id, left, right);
	}
}