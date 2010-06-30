package ch.ethz.mergesort;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class WorstCaseLocalitySortingWorker extends SortingWorker {
	public WorstCaseLocalitySortingWorker(int id, int size) {
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

class WorstCaseLocalityMergingWorker extends MergingWorker {
	public WorstCaseLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
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
	public WorstCaseLocalityMergeSortTest() {
		super();
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new WorstCaseLocalitySortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new WorstCaseLocalityMergingWorker(id, left, right);
	}
}