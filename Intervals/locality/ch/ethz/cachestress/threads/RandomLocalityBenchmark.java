package ch.ethz.cachestress.threads;

import java.util.Random;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomLocalityCacheStressWorker extends CacheStressWorker {
	private int units;
	private Random random;

	public RandomLocalityCacheStressWorker(int id, int[] array) {
		super(id, array);
		units = Main.units.size();
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(units)));
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
		return new RandomLocalityCacheStressWorker(id, array);
	}
}
