package ch.ethz.cachestress.intervals;

import ch.ethz.cachestress.Main;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int units, workerPerUnit;

	public Benchmark() {
		this.units = Main.units.size();
		this.workerPerUnit = Main.workerPerUnit;
	}

	public abstract void createCacheStressTask(Dependency dep, int id,
			int[] array);

	public long run() {
		startBenchmark();

		final int[] array1 = createRandomIntegerArray(Main.arraySize);
		final int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create intervals
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				for (int i = 0; i < workerPerUnit * units; i++) {
					if ((i % units) < 4) {
						createCacheStressTask(subinterval, i, array1);
					} else {
						createCacheStressTask(subinterval, i, array2);
					}
				}
			}
		});

		return stopBenchmark();
	}
}
