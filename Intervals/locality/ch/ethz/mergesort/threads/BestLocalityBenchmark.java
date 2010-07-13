package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class BestLocalitySortingWorker extends SortingWorker {
	public BestLocalitySortingWorker(int id, int size) {
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

class BestLocalityMergingWorker extends MergingWorker {
	public BestLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(left.id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new BestLocalitySortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new BestLocalityMergingWorker(id, left, right);
	}
}
