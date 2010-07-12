package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Config;
import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class WorstCacheStressWorker extends CacheStressWorker {
	private int units;

	public WorstCacheStressWorker(int id, int[] array) {
		super(id, array);
		units = Config.units.size();
	}

	public void run() {
		try {
			int unit = id;

			if (unit % 2 == 1) {
				unit = (unit + (units / 2)) % units;
			}

			// Debug output
			// System.out.printf("ID: %d, Unit: %d\n", getWorkerId(), unit);

			Affinity.set(Config.units.get(unit));
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.run();
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new WorstCacheStressWorker(id, array);
	}
}
