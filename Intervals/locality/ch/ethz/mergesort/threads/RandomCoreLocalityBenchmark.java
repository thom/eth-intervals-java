package ch.ethz.mergesort.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class RandomCoreLocalitySortingTask extends SortingTask {
	private Random random;
	private int units;

	public RandomCoreLocalitySortingTask(int id, int unit, int size) {
		super(id, unit, size);
		random = new Random();
		units = Main.places.unitsLength;
	}

	public void run() {
		try {
			Affinity.set(Main.places.getUnit(random.nextInt(units)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomCoreLocalityMergingTask extends MergingTask {
	private Random random;
	private int units;

	public RandomCoreLocalityMergingTask(int id, int unit, MergeSortTask left,
			MergeSortTask right) {
		super(id, unit, left, right);
		random = new Random();
		units = Main.places.unitsLength;
	}

	public void run() {
		try {
			Affinity.set(Main.places.getUnit(random.nextInt(units)));
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
	public SortingTask createSortingTask(int id, int unit, int size) {
		return new RandomCoreLocalitySortingTask(id, unit, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int unit, MergeSortTask left,
			MergeSortTask right) {
		return new RandomCoreLocalityMergingTask(id, unit, left, right);
	}
}