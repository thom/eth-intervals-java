package ch.ethz.cachestress.threads;

class IgnorantLocalityCacheStressTask extends CacheStressTask {
	public IgnorantLocalityCacheStressTask(int id, int[] array) {
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
	public CacheStressTask createCacheStressTask(int id, int[] array) {
		return new IgnorantLocalityCacheStressTask(id, array);
	}
}
