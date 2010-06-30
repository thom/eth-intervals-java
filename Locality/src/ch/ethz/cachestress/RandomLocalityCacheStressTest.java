package ch.ethz.cachestress;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomLocalityWorker extends CacheStressWorker {
	private int units;
	private Random random;

	public RandomLocalityWorker(int id, int[] array) {
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

public class RandomLocalityCacheStressTest extends CacheStressTest {
	public RandomLocalityCacheStressTest() {
		super();
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new RandomLocalityWorker(id, array);
	}
}
