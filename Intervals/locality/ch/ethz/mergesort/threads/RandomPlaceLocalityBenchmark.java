package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomPlaceLocalitySortingTask extends SortingTask {
	private Random random;
	private int places;

	public RandomPlaceLocalitySortingTask(int id, int unit, int size) {
		super(id, unit, size);
		random = new Random();
		places = Main.places.length;
	}

	public void run() {
		try {
			Affinity.set(Main.places.get(random.nextInt(places)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomPlaceLocalityMergingTask extends MergingTask {
	private Random random;
	private int places;

	public RandomPlaceLocalityMergingTask(int id, int unit, MergeSortTask left,
			MergeSortTask right) {
		super(id, unit, left, right);
		random = new Random();
		places = Main.places.length;
	}

	public void run() {
		try {
			Affinity.set(Main.places.get(random.nextInt(places)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomPlaceLocalityBenchmark extends Benchmark {
	public RandomPlaceLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(int id, int unit, int size) {
		return new RandomPlaceLocalitySortingTask(id, unit, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int unit, MergeSortTask left,
			MergeSortTask right) {
		return new RandomPlaceLocalityMergingTask(id, unit, left, right);
	}
}