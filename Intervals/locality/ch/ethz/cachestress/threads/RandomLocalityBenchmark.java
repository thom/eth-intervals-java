package ch.ethz.cachestress.threads;

import java.util.Random;

import ch.ethz.cachestress.Config;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomCacheStressWorker extends CacheStressWorker {
	private int units;
	private Random random;

	public RandomCacheStressWorker(int id, int[] array) {
		super(id, array);
		units = Config.units.size();
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(random.nextInt(units)));
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
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new RandomCacheStressWorker(id, array);
	}
}
