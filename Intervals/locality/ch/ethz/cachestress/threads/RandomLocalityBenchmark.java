package ch.ethz.cachestress.threads;

import java.util.Random;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomLocalityCacheStressTask extends CacheStressTask {
	private int units;
	private Random random;

	public RandomLocalityCacheStressTask(int id, int[] array) {
		super(id, array);
		units = Main.places.unitsLength;
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.places.getUnit(random.nextInt(units)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.run();
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super();
	}

	@Override
	public CacheStressTask createCacheStressTask(int id, int[] array) {
		return new RandomLocalityCacheStressTask(id, array);
	}
}
