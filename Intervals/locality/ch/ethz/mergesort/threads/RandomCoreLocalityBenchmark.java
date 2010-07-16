package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomCoreLocalitySortingWorker extends SortingWorker {
	private Random random;
	private int units;

	public RandomCoreLocalitySortingWorker(int id, int unit, int size) {
		super(id, unit, size);
		random = new Random();
		units = Main.units.size();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(units)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomCoreLocalityMergingWorker extends MergingWorker {
	private Random random;
	private int units;

	public RandomCoreLocalityMergingWorker(int id, int unit,
			MergeSortWorker left, MergeSortWorker right) {
		super(id, unit, left, right);
		random = new Random();
		units = Main.units.size();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(units)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomCoreLocalityBenchmark extends Benchmark {
	public RandomCoreLocalityBenchmark() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int unit, int size) {
		return new RandomCoreLocalitySortingWorker(id, unit, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int unit,
			MergeSortWorker left, MergeSortWorker right) {
		return new RandomCoreLocalityMergingWorker(id, unit, left, right);
	}
}