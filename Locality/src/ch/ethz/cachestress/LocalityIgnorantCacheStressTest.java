package ch.ethz.cachestress;

class LocalityIgnorantWorker extends CacheStressWorker {
	public LocalityIgnorantWorker(int id, int[] array) {
		super(id, array);
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
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new LocalityIgnorantWorker(id, array);
	}
}
