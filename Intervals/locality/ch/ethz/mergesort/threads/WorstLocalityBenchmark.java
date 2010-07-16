package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class WorstLocalitySortingWorker extends SortingWorker {
	public WorstLocalitySortingWorker(int id, int node, int size) {
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

class WorstLocalityMergingWorker extends MergingWorker {
	public WorstLocalityMergingWorker(int id, int node, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, node, left, right);
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode((node + 1) % Main.units.nodesSize()));
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
	public SortingWorker createSortingWorker(int id, int node, int size) {
		return new WorstLocalitySortingWorker(id, node, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int node,
			MergeSortWorker left, MergeSortWorker right) {
		return new WorstLocalityMergingWorker(id, node, left, right);
	}
}