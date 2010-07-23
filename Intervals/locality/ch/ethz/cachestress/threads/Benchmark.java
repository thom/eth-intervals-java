package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Main;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int units, tasksPerUnit;

	public Benchmark() {
		this.units = Main.units.size();
		this.tasksPerUnit = Main.tasksPerUnit;
	}

	public abstract CacheStressTask createCacheStressTask(int id,
			int[] array);

	public long run() {
		startBenchmark();

		CacheStressTask[] tasks = new CacheStressTask[units];
		int[] array1 = createRandomIntegerArray(Main.arraySize);
		int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create tasks
		for (int i = 0; i < tasksPerUnit * units; i++) {
			if ((i % units) < 4) {
				tasks[i] = createCacheStressTask(i, array1);
			} else {
				tasks[i] = createCacheStressTask(i, array2);
			}
		}

		// Start tasks
		for (int i = 0; i < tasksPerUnit * units; i++) {
			tasks[i].start();
		}

		// Wait for tasks to finish
		for (int i = 0; i < tasksPerUnit * units; i++) {
			try {
				tasks[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return stopBenchmark();
	}
}
