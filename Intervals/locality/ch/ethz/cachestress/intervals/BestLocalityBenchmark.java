package ch.ethz.cachestress.intervals;

import ch.ethz.intervals.Dependency;

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, int id, int[] array) {
		// TODO: Set correct place
		// try {
		// Affinity.set(Main.units.get(id % Main.units.size()));
		// } catch (SetAffinityException e) {
		// e.printStackTrace();
		// System.exit(1);
		// }

		new CacheStressTask(dep, null, id, array);
	}
}
