package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomNodeLocalitySortingWorker extends SortingWorker {
	private Random random;

	public RandomNodeLocalitySortingWorker(int id, int unit, int size) {
		super(id, unit, size);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(random.nextInt(Main.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomNodeLocalityMergingWorker extends MergingWorker {
	private Random random;

	public RandomNodeLocalityMergingWorker(int id, int unit,
			MergeSortWorker left, MergeSortWorker right) {
		super(id, unit, left, right);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(random.nextInt(Main.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomNodeLocalityBenchmark extends Benchmark {
	public RandomNodeLocalityBenchmark() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int unit, int size) {
		return new RandomNodeLocalitySortingWorker(id, unit, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int unit,
			MergeSortWorker left, MergeSortWorker right) {
		return new RandomNodeLocalityMergingWorker(id, unit, left, right);
	}
}