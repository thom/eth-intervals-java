package ch.ethz.cachestress;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class WorstCaseLocalityWorker extends CacheStressWorker {
	private int units;

	public WorstCaseLocalityWorker(int id, int[] array) {
		super(id, array);
		units = Config.units.size();
	}

	public void run() {
		try {
			// Debug output
			//System.out.printf("ID: %d, Unit: %d\n", getWorkerId(), Config.units
			//		.get((getWorkerId() + (units / 2)) % units));
			Affinity.set(Config.units
					.get((getWorkerId() + (units / 2)) % units));
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.run();
	}
}

public class WorstCaseLocalityCacheStressTest extends CacheStressTest {
	public WorstCaseLocalityCacheStressTest(int arraySize) {
		super(arraySize);
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new WorstCaseLocalityWorker(id, array);
	}
}
