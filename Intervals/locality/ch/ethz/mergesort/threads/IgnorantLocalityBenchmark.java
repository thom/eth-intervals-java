package ch.ethz.mergesort.threads;

class IgnorantLocalitySortingWorker extends SortingWorker {
	public IgnorantLocalitySortingWorker(int id, int size) {
		super(id, size);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityMergingWorker extends MergingWorker {
	public IgnorantLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
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
	public SortingWorker createSortingWorker(int id, int size) {
		return new IgnorantLocalitySortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new IgnorantLocalityMergingWorker(id, left, right);
	}
}
