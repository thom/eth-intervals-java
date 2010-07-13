package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Main;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	private int units;

	public Benchmark() {
		this.units = Main.units.size();
	}

	public abstract CacheStressWorker createCacheStressWorker(int id,
			int[] array);

	public long run() {
		startBenchmark();

		CacheStressWorker[] workers = new CacheStressWorker[units];
		int[] array1 = createRandomIntegerArray(Main.arraySize);
		int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create workers
		for (int i = 0; i < units; i++) {
			if (i < 4) {
				workers[i] = createCacheStressWorker(i, array1);
			} else {
				workers[i] = createCacheStressWorker(i, array2);
			}
		}

		// Start workers
		for (int i = 0; i < units; i++) {
			workers[i].start();
		}

		// Wait for workers to finish
		for (int i = 0; i < units; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return stopBenchmark();
	}
}
