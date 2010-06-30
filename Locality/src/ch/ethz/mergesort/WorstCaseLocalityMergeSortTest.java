package ch.ethz.mergesort;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class WorstCaseLocalitySortingWorker extends SortingWorker {
	public WorstCaseLocalitySortingWorker(int id, int[] sharedArray, int start,
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

class WorstCaseLocalityMergingWorker extends MergingWorker {
	public WorstCaseLocalityMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		super(id, sharedArray, left, right);
	}

	public void run() {
		try {
			Affinity.set(Config.units.get((left.id + (Config.units.size() / 2))
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
	public SortingWorker createSortingWorker(int id, int[] sharedArray,
			int start, int end, int upperBound) {
		return new WorstCaseLocalitySortingWorker(id, sharedArray, start, end,
				upperBound);
	}

	@Override
	public MergingWorker createMergingWorker(int id, int[] sharedArray,
			MergeSortWorker left, MergeSortWorker right) {
		return new WorstCaseLocalityMergingWorker(id, sharedArray, left, right);
	}
}