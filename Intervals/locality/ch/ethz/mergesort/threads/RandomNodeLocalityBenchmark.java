package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomNodeLocalitySortingTask extends SortingTask {
	private Random random;

	public RandomNodeLocalitySortingTask(int id, int unit, int size) {
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

class RandomNodeLocalityMergingTask extends MergingTask {
	private Random random;

	public RandomNodeLocalityMergingTask(int id, int unit,
			MergeSortTask left, MergeSortTask right) {
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
	public SortingTask createSortingTask(int id, int unit, int size) {
		return new RandomNodeLocalitySortingTask(id, unit, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int unit,
			MergeSortTask left, MergeSortTask right) {
		return new RandomNodeLocalityMergingTask(id, unit, left, right);
	}
}