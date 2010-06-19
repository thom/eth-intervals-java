package ch.ethz.cachestress;

import java.util.Arrays;
import java.util.Random;

import ch.ethz.hwloc.MafushiPlace;
import ch.ethz.hwloc.Place;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.util.StopWatch;

class LocalityAwareWorker extends Thread {
	private int sharedArray[];
	private Place place;
	private int placeNumber, begin, end;

	public LocalityAwareWorker(int[] sharedArray, int placeNumber, int begin,
			int end) {
		this.sharedArray = sharedArray;
		this.begin = begin;
		this.end = end;
		this.placeNumber = placeNumber;

		place = new MafushiPlace();
	}

	public void run() {
		try {
			place.set(placeNumber);
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

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
}

public class LocalityAwareCacheStressTest {
	public final static int ARRAY_SIZE = 4194296;

	public static void main(String[] args) {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		LocalityAwareWorker[] worker = new LocalityAwareWorker[8];
		int[] array = createRandomIntegerArray(ARRAY_SIZE);
		int sliceSize = 4194296 / 8;

		// Create worker
		for (int i = 0; i < 8; i++) {
			int begin = i * sliceSize;
			int end = (i + 1) * sliceSize;
			System.out.printf("Worker %d for array slice [%d, %d)\n", i, begin,
					end);
			worker[i] = new LocalityAwareWorker(array, i, begin, end);
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
		System.out.println("\nElapsed time: " + stopWatch.getElapsedTime()
				+ "ms");
	}

	private static int[] createRandomIntegerArray(int size) {
		Random random = new Random();
		int[] tmp = new int[size];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = random.nextInt();
		}

		return tmp;
	}
}
