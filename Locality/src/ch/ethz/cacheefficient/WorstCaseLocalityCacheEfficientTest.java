package ch.ethz.cacheefficient;

class WorstCaseLocalityMainWorker extends MainWorker {
	public WorstCaseLocalityMainWorker(int id) {
		super(id);
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

	public void run() {
		// TODO: Set worst-case locality

		super.run();
	}
}

class WorstCaseLocalitySortingWorker extends SortingWorker {
	public WorstCaseLocalitySortingWorker(int id, int size) {
		super(id, size);
	}

	public void run() {
		// TODO: Set worst-case locality

		super.run();
	}
}

class WorstCaseLocalityMergingWorker extends MergingWorker {
	public WorstCaseLocalityMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
	}

	public void run() {
		// TODO: Set worst-case locality

		super.run();
	}
}

public class WorstCaseLocalityCacheEfficientTest extends CacheEfficientTest {
	public WorstCaseLocalityCacheEfficientTest() {
		super();
	}

	@Override
	public MainWorker createMainWorker(int id) {
		return new WorstCaseLocalityMainWorker(id);
	}
}