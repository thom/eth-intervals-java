package ch.ethz.cachestress;

import java.util.Random;

import ch.ethz.hwloc.MafushiPlace;
import ch.ethz.hwloc.Place;
import ch.ethz.util.StopWatch;

class CacheStressWorker extends Thread {
	private int id;
	private int array[];
	private Place place;

	public CacheStressWorker(int id, int[] array) {
		super("cache-stress-worker-" + id);
		this.id = id;
		this.array = array;
		place = new MafushiPlace();
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

	public int getWorkerId() {
		return id;
	}

	public Place getPlace() {
		return place;
	}
}

public abstract class CacheStressTest {
	private int arraySize;

	public CacheStressTest(int arraySize) {
		this.arraySize = arraySize;
	}

	public abstract CacheStressWorker createCacheStressWorker(int id,
			int[] array);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		CacheStressWorker[] worker = new CacheStressWorker[8];
		int[] array1 = createRandomIntegerArray(arraySize);
		int[] array2 = createRandomIntegerArray(arraySize);

		// Create worker
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				worker[i] = createCacheStressWorker(i, array1);
			} else {
				worker[i] = createCacheStressWorker(i, array2);
			}
		}

		// Start worker
		for (int i = 0; i < 8; i++) {
			worker[i].start();
		}

		// Wait for worker to finish
		for (int i = 0; i < 8; i++) {
			try {
				worker[i].join();
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
