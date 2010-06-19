package ch.ethz.cachestress;

class LocalityIgnorantWorker extends CacheStressWorker {
	public LocalityIgnorantWorker(int id, int[] sharedArray, int begin, int end) {
		super(id, sharedArray, begin, end);
	}

	public void run() {
		super.run();
	}
}

public class LocalityIgnorantCacheStressTest extends CacheStressTest {
	public LocalityIgnorantCacheStressTest(int arraySize) {
		super(arraySize);
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array,
			int begin, int end) {
		return new LocalityIgnorantWorker(id, array, begin, end);
	}
}
