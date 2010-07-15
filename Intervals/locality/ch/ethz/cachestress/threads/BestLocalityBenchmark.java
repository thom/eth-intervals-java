package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class BestLocalityCacheStressWorker extends CacheStressWorker {
	public BestLocalityCacheStressWorker(int id, int[] array) {
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
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new BestLocalityCacheStressWorker(id, array);
	}
}
