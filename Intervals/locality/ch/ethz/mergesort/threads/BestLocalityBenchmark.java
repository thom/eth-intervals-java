package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class BestLocalitySortingWorker extends SortingWorker {
	public BestLocalitySortingWorker(int id, int node, int size) {
		super(id, node, size);
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(node));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class BestLocalityMergingWorker extends MergingWorker {
	public BestLocalityMergingWorker(int id, int node, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, node, left, right);
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(node));
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
	public SortingWorker createSortingWorker(int id, int node, int size) {
		return new BestLocalitySortingWorker(id, node, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int node,
			MergeSortWorker left, MergeSortWorker right) {
		return new BestLocalityMergingWorker(id, node, left, right);
	}
}
