package ch.ethz.cachestress.intervals;

import ch.ethz.intervals.Dependency;

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, int id, int[] array) {
		new CacheStressTask(dep, null, id, array);
	}
}
