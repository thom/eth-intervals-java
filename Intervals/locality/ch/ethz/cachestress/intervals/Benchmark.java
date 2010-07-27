package ch.ethz.cachestress.intervals;

import ch.ethz.cachestress.Main;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int units, tasksPerUnit;

	public Benchmark() {
		this.units = Main.places.unitsLength;
		this.tasksPerUnit = Main.tasksPerUnit;
	}

	public abstract void createCacheStressTask(Dependency dep, PlaceID placeID,
			int id, int[] array);

	public long run() {
		startBenchmark();

		final int[] array1 = createRandomIntegerArray(Main.arraySize);
		final int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create intervals
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				for (int i = 0; i < tasksPerUnit * units; i++) {
					if ((i % units) < (units / 2)) {
						createCacheStressTask(subinterval,
								Main.places.getPlaceID(0), i, array1);
					} else {
						createCacheStressTask(subinterval,
								Main.places.getPlaceID(1), i, array2);
					}
				}
			}
		});

		return stopBenchmark();
	}
}
