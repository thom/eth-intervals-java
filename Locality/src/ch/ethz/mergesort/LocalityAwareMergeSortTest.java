package ch.ethz.mergesort;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareSortingWorker extends SortingWorker {
	public LocalityAwareSortingWorker(int id, int size) {
		super(id, size);
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
	public LocalityAwareMergeSortTest() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new LocalityAwareSortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new LocalityAwareMergingWorker(id, left, right);
	}
}
