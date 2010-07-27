package ch.ethz.cachestress.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, PlaceID placeID, int id,
			int[] array) {
		new CacheStressTask(dep, null, id, array);
	}
}
