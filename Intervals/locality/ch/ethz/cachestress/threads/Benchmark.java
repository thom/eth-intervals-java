package ch.ethz.cachestress.threads;

import java.util.Random;

import ch.ethz.cachestress.Main;
import ch.ethz.util.LocalityBenchmark;
import ch.ethz.util.StopWatch;

abstract class CacheStressWorker extends Thread {
	protected final int id;
	protected final int array[];

	public CacheStressWorker(int id, int[] array) {
		super("cache-stress-worker-" + id);
		this.id = id;
		this.array = array;
	}

	public void run() {
		for (int k = 0; k < 100; k++) {
			// Sum up array slice
			int sum = 0;
			for (int i = 0; i < array.length; i++) {
				sum += array[i];
			}
			// Multiply array slice
			int mult = 1;
			for (int i = 0; i < array.length; i++) {
				mult *= array[i];
			}
		}
	}
}

public abstract class Benchmark extends LocalityBenchmark {
	private int units;

	public Benchmark() {
		this.units = Main.units.size();
	}

	public abstract CacheStressWorker createCacheStressWorker(int id,
			int[] array);

	public long run() {
		StopWatch stopWatch = new StopWatch();
		cleanJvm();

		// Start stop watch
		stopWatch.start();

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

		// Stop stop watch
		stopWatch.stop();
		cleanJvm();
		return stopWatch.getElapsedTime();
	}

	private int[] createRandomIntegerArray(int size) {
		Random random = new Random();
		int[] tmp = new int[size];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = random.nextInt();
		}

		return tmp;
	}
}
