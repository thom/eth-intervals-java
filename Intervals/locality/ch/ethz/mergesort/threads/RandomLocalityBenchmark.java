package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomLocalitySortingWorker extends SortingWorker {
	private Random random;

	public RandomLocalitySortingWorker(int id, int size) {
		super(id, size);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(Main.units.size())));
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
			Affinity.set(Main.units.get(random.nextInt(Main.units.size())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new RandomLocalitySortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new RandomLocalityMergingWorker(id, left, right);
	}
}