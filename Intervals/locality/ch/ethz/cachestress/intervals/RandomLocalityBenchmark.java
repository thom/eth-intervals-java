package ch.ethz.cachestress.intervals;

import ch.ethz.intervals.Dependency;

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, int id, int[] array) {
		// TODO: Set correct place
		// try {
		// Affinity.set(Main.units.get(random.nextInt(units)));
		// } catch (SetAffinityException e) {
		// e.printStackTrace();
		// System.exit(1);
		// }

		new CacheStressTask(dep, null, id, array);
	}
}
