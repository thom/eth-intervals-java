package ch.ethz.mergesort;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareSortingWorker extends SortingWorker {
	public LocalityAwareSortingWorker(int id, int[] sharedArray, int start,
			int end, int upperBound) {
		super(id, sharedArray, start, end, upperBound);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(this.id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class LocalityAwareMergingWorker extends MergingWorker {
	public LocalityAwareMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		super(id, sharedArray, left, right);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(left.id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class LocalityAwareMergeSortTest extends MergeSortTest {
	public LocalityAwareMergeSortTest(int arraySize, int upperBound) {
		super(arraySize, upperBound);
	}

	@Override
	public SortingWorker createSortingWorker(int id, int[] sharedArray,
			int start, int end, int upperBound) {
		return new LocalityAwareSortingWorker(id, sharedArray, start, end,
				upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		return new LocalityAwareMergingWorker(id, sharedArray, left, right);
	}
}
