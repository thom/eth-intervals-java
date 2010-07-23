package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class BestLocalitySortingTask extends SortingTask {
	public BestLocalitySortingTask(int id, int node, int size) {
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

class BestLocalityMergingTask extends MergingTask {
	public BestLocalityMergingTask(int id, int node, MergeSortTask left,
			MergeSortTask right) {
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
	public SortingTask createSortingTask(int id, int node, int size) {
		return new BestLocalitySortingTask(id, node, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int node,
			MergeSortTask left, MergeSortTask right) {
		return new BestLocalityMergingTask(id, node, left, right);
	}
}
