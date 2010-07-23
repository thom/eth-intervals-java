package ch.ethz.mergesort.threads;

class IgnorantLocalitySortingTask extends SortingTask {
	public IgnorantLocalitySortingTask(int id, int node, int size) {
		super(id, node, size);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityMergingTask extends MergingTask {
	public IgnorantLocalityMergingTask(int id, int node,
			MergeSortTask left, MergeSortTask right) {
		super(id, node, left, right);
	}

	public void run() {
		super.run();
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super();
	}

	@Override
	public SortingTask createSortingTask(int id, int node, int size) {
		return new IgnorantLocalitySortingTask(id, node, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int node,
			MergeSortTask left, MergeSortTask right) {
		return new IgnorantLocalityMergingTask(id, node, left, right);
	}
}
