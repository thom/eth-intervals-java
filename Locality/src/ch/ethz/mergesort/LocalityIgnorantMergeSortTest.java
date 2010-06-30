package ch.ethz.mergesort;

class LocalityIgnorantSortingWorker extends SortingWorker {
	public LocalityIgnorantSortingWorker(int id, int[] sharedArray,
			int start, int end, int upperBound) {
		super(id, sharedArray, start, end, upperBound);
	}

	public void run() {
		super.run();
	}
}

class LocalityIgnorantMergingWorker extends MergingWorker {
	public LocalityIgnorantMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		super(id, sharedArray, left, right);
	}

	public void run() {
		super.run();
	}
}

public class LocalityIgnorantMergeSortTest extends MergeSortTest {
	public LocalityIgnorantMergeSortTest(int arraySize, int upperBound) {
		super(arraySize, upperBound);
	}

	@Override
	public SortingWorker createSortingWorker(int id, int[] sharedArray,
			int start, int end, int upperBound) {
		return new LocalityIgnorantSortingWorker(id, sharedArray, start, end,
				upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		return new LocalityIgnorantMergingWorker(id, sharedArray, left, right);
	}
}
