package ch.ethz.cachestress.intervals;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, PlaceID placeID, int id,
			int[] array) {
		if (id % 2 == 0) {
			placeID = Main.places.getPlaceID(placeID.id + 1);
		}

		// Debug output
		System.out.printf("ID: %d, Place: %d\n", id, placeID.id);

		new CacheStressTask(dep, placeID, id, array);
	}
}
