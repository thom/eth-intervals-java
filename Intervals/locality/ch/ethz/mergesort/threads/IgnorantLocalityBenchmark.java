package ch.ethz.mergesort.threads;

class IgnorantLocalitySortingTask extends SortingTask {
	public IgnorantLocalitySortingTask(int id, int place, int size) {
		super(id, place, size);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityMergingTask extends MergingTask {
	public IgnorantLocalityMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		super(id, place, left, right);
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
	public SortingTask createSortingTask(int id, int place, int size) {
		return new IgnorantLocalitySortingTask(id, place, size);
	}

	@Override
	public MergingTask createMergingTask(int id, int place, MergeSortTask left,
			MergeSortTask right) {
		return new IgnorantLocalityMergingTask(id, place, left, right);
	}
}
