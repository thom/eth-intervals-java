package ch.ethz.cachestress;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareWorker extends CacheStressWorker {
	public LocalityAwareWorker(int id, int[] array) {
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

public class LocalityAwareCacheStressTest extends CacheStressTest {
	public LocalityAwareCacheStressTest() {
		super();
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new LocalityAwareWorker(id, array);
	}
}
