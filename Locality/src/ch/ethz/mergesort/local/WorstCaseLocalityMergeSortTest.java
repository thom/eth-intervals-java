package ch.ethz.mergesort.local;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class WorstCaseLocalitySortingWorker extends SortingWorker {
	public WorstCaseLocalitySortingWorker(int id, int size, int upperBound) {
		super(id, size, upperBound);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(this.getWorkerId()));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class WorstCaseLocalityMergingWorker extends MergingWorker {
	public WorstCaseLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
	}

	public void run() {
		try {
			Affinity.set(Config.units
					.get((getLeft().getWorkerId() + (Config.units.size() / 2))
							% Config.units.size()));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class WorstCaseLocalityMergeSortTest extends MergeSortTest {
	public WorstCaseLocalityMergeSortTest(int arraySize, int upperBound) {
		super(arraySize, upperBound);
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size, int upperBound) {
		return new WorstCaseLocalitySortingWorker(id, size, upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new WorstCaseLocalityMergingWorker(id, left, right);
	}
}