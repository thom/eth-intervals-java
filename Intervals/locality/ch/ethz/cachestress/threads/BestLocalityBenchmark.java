package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class BestLocalityCacheStressTask extends CacheStressTask {
	public BestLocalityCacheStressTask(int id, int[] array) {
		super(id, array);
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(id % Main.units.size()));
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}

		super.run();
	}
}

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public CacheStressTask createCacheStressTask(int id, int[] array) {
		return new BestLocalityCacheStressTask(id, array);
	}
}
