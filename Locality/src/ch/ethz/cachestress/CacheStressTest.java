package ch.ethz.cachestress;

import java.util.Arrays;
import java.util.Random;

import ch.ethz.hwloc.MafushiPlace;
import ch.ethz.hwloc.Place;
import ch.ethz.util.StopWatch;

class CacheStressWorker extends Thread {
	private int id;
	private int sharedArray[];
	private Place place;
	private int begin, end;

	public CacheStressWorker(int id, int[] sharedArray, int begin, int end) {
		super("cache-stress-worker-" + id);
		this.id = id;
		this.sharedArray = sharedArray;
		this.begin = begin;
		this.end = end;
		place = new MafushiPlace();
	}

	public void run() {
		// Sum up array slice
		int sum = 0;
		for (int i = begin; i < end; i++) {
			sum += sharedArray[i];
		}
		// Multiply array slice
		int mult = 1;
		for (int i = begin; i < end; i++) {
			mult *= sharedArray[i];
		}
		// Sort array slice
		Arrays.sort(sharedArray, begin, end);
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
			int[] array, int begin, int end);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		CacheStressWorker[] worker = new CacheStressWorker[8];
		int[] array = createRandomIntegerArray(arraySize);
		int sliceSize = 4194296 / 8;

		// Create worker
		for (int i = 0; i < 8; i++) {
			int begin = i * sliceSize;
			int end = (i + 1) * sliceSize;
			worker[i] = createCacheStressWorker(i, array, begin, end);
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
