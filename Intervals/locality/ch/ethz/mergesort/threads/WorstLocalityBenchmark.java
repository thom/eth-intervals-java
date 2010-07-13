package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class WorstLocalitySortingWorker extends SortingWorker {
	public WorstLocalitySortingWorker(int id, int size) {
		super(id, size);
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class WorstLocalityMergingWorker extends MergingWorker {
	public WorstLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
	}

	public void run() {
		try {
			Affinity.set(Main.units.get((left.id + (Main.units.size() / 2))
					% Main.units.size()));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new WorstLocalitySortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new WorstLocalityMergingWorker(id, left, right);
	}
}