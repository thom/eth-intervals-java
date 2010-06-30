package ch.ethz.mergesort.local;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareSortingWorker extends SortingWorker {
	public LocalityAwareSortingWorker(int id, int size, int upperBound) {
		super(id, size, upperBound);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class LocalityAwareMergingWorker extends MergingWorker {
	public LocalityAwareMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
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
	public SortingWorker createSortingWorker(int id, int size, int upperBound) {
		return new LocalityAwareSortingWorker(id, size, upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new LocalityAwareMergingWorker(id, left, right);
	}
}
