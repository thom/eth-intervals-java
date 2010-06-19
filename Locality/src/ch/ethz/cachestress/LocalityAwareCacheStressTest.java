package ch.ethz.cachestress;

import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareWorker extends CacheStressWorker {
	public LocalityAwareWorker(int id, int[] sharedArray, int begin, int end) {
		super(id, sharedArray, begin, end);
	}

	public void run() {
		try {
			getPlace().set(getWorkerId());
			System.out.println(getPlace());
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}

		super.run();
	}
}

public class LocalityAwareCacheStressTest extends CacheStressTest {
	public LocalityAwareCacheStressTest(int arraySize) {
		super(arraySize);
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array,
			int begin, int end) {
		return new LocalityAwareWorker(id, array, begin, end);
	}
}
