package ch.ethz.mergesort.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.mergesort.Main;

class WorstLocalitySortingTask extends SortingTask {
	public WorstLocalitySortingTask(int id, int node, int size) {
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

class WorstLocalityMergingTask extends MergingTask {
	public WorstLocalityMergingTask(int id, int node, MergeSortTask left,
			MergeSortTask right) {
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
	public SortingTask createSortingTask(int id, int node, int size) {
		return new WorstLocalitySortingTask(id, node, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int node,
			MergeSortTask left, MergeSortTask right) {
		return new WorstLocalityMergingTask(id, node, left, right);
	}
}