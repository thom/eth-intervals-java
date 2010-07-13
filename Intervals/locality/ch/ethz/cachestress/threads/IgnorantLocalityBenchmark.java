package ch.ethz.cachestress.threads;

class IgnorantLocalityCacheStressWorker extends CacheStressWorker {
	public IgnorantLocalityCacheStressWorker(int id, int[] array) {
		super(id, array);
	}

	public void run() {
		super.run();
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array) {
		return new IgnorantLocalityCacheStressWorker(id, array);
	}
}
