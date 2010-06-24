package ch.ethz.cachestress;

import java.util.Random;

import ch.ethz.util.StopWatch;

class CacheStressWorker extends Thread {
	private int id;
	private int array[];

	public CacheStressWorker(int id, int[] array) {
		super("cache-stress-worker-" + id);
		this.id = id;
		this.array = array;
	}

	public int getWorkerId() {
		return id;
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

public abstract class CacheStressTest {
	private int arraySize;
	private int units;

	public CacheStressTest(int arraySize) {
		this.arraySize = arraySize;
		this.units = Config.units.size();
	}

	public abstract CacheStressWorker createCacheStressWorker(int id,
			int[] array);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		CacheStressWorker[] workers = new CacheStressWorker[units];
		int[] array1 = createRandomIntegerArray(arraySize);
		int[] array2 = createRandomIntegerArray(arraySize);

		// Create worker
		for (int i = 0; i < units; i++) {
			if (i < 4) {
				workers[i] = createCacheStressWorker(i, array1);
			} else {
				workers[i] = createCacheStressWorker(i, array2);
			}
		}

		// Start worker
		for (int i = 0; i < units; i++) {
			workers[i].start();
		}

		// Wait for worker to finish
		for (int i = 0; i < units; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Stop stop watch
		stopWatch.stop();
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
