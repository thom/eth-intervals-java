package ch.ethz.cachestress.intervals;

import ch.ethz.intervals.Dependency;

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public void createCacheStressTask(Dependency dep, int id, int[] array) {
		// TODO: Set correct place
		// int unit = id;
		//
		// if (unit % 2 == 1) {
		// unit = (unit + (units / 2)) % units;
		// }
		//
		// // Debug output
		// // System.out.printf("ID: %d, Unit: %d\n", getWorkerId(), unit);
		//
		// Affinity.set(Main.units.get(unit));

		new CacheStressTask(dep, null, id, array);
	}
}
