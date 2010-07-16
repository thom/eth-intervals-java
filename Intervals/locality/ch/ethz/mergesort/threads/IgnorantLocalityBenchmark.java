package ch.ethz.mergesort.threads;

class IgnorantLocalitySortingWorker extends SortingWorker {
	public IgnorantLocalitySortingWorker(int id, int node, int size) {
		super(id, node, size);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityMergingWorker extends MergingWorker {
	public IgnorantLocalityMergingWorker(int id, int node,
			MergeSortWorker left, MergeSortWorker right) {
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
	public SortingWorker createSortingWorker(int id, int node, int size) {
		return new IgnorantLocalitySortingWorker(id, node, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int node,
			MergeSortWorker left, MergeSortWorker right) {
		return new IgnorantLocalityMergingWorker(id, node, left, right);
	}
}
