package ch.ethz.cachestress.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.ethz.cachestress.Main;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	protected ExecutorService exec;
	private int units, tasksPerUnit;

	public Benchmark() {
		this.units = Main.places.unitsLength;
		this.tasksPerUnit = Main.tasksPerUnit;
	}

	public CacheStressTask createCacheStressTask(int id, int[] array) {
		return new CacheStressTask(id, array);
	}

	public long run() {
		startBenchmark();
		exec = Executors.newFixedThreadPool(Main.threads);

		Future<?>[] tasks = new Future<?>[tasksPerUnit * units];
		int[] array1 = createRandomIntegerArray(Main.arraySize);
		int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Submit tasks
		for (int i = 0; i < tasksPerUnit * units; i++) {
			if ((i % units) < 4) {
				tasks[i] = exec.submit(createCacheStressTask(i, array1));
			} else {
				tasks[i] = exec.submit(createCacheStressTask(i, array2));
			}
		}

		// Wait for tasks to finish
		for (int i = 0; i < tasksPerUnit * units; i++) {
			try {
				tasks[i].get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		exec.shutdown();
		return stopBenchmark();
	}
}
