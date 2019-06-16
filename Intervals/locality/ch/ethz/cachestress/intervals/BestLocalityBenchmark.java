package ch.ethz.cachestress.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, PlaceID placeID, int id,
			int[] array) {
		new CacheStressTask(dep, placeID, id, array);
	}
}
