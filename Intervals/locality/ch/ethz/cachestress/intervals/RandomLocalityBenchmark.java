package ch.ethz.cachestress.intervals;

import java.util.Random;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;

public class RandomLocalityBenchmark extends Benchmark {
	private Random random;

	public RandomLocalityBenchmark() {
		super();
		random = new Random();
	}

	@Override
	public void createCacheStressTask(Dependency dep, PlaceID placeID, int id,
			int[] array) {
		new CacheStressTask(dep, Main.places.getPlaceID(random
				.nextInt(Main.places.length)), id, array);
	}
}
