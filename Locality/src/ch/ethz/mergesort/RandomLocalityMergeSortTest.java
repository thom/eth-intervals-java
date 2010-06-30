package ch.ethz.mergesort;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomLocalitySortingWorker extends SortingWorker {
	private Random random;

	public RandomLocalitySortingWorker(int id, int size, int upperBound) {
		super(id, size, upperBound);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(random.nextInt(Config.units.size())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomLocalityMergingWorker extends MergingWorker {
	private Random random;

	public RandomLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(random.nextInt(Config.units.size())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomLocalityMergeSortTest extends MergeSortTest {
	public RandomLocalityMergeSortTest(int arraySize, int upperBound) {
		super(arraySize, upperBound);
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size, int upperBound) {
		return new RandomLocalitySortingWorker(id, size, upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new RandomLocalityMergingWorker(id, left, right);
	}
}