package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Config;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class BestCacheStressWorker extends CacheStressWorker {
	public BestCacheStressWorker(int id, int[] array) {
		super(id, array);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(id));
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
		return new BestCacheStressWorker(id, array);
	}
}
