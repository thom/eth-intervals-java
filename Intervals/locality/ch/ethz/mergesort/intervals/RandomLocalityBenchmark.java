package ch.ethz.mergesort.intervals;

import java.util.Random;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.mergesort.Main;

public class RandomLocalityBenchmark extends Benchmark {
	private Random random;

	public RandomLocalityBenchmark() {
		super();
		random = new Random();
	}

	@Override
	public SortingTask createSortingTask(Dependency dep, PlaceID placeID,
			int id, int size) {
		return new SortingTask(dep, Main.places.getPlaceID(random
				.nextInt(Main.places.length)), id, size);
	}

	@Override
	public MergingTask createMergingTask(Dependency dep, PlaceID placeID,
			int id, MergeSortTask left, MergeSortTask right) {
		return new MergingTask(dep, Main.places.getPlaceID(random
				.nextInt(Main.places.length)), id, left, right);
	}
}