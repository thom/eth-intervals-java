package ch.ethz.cacheefficient;

class LocalityIgnorantMainWorker extends MainWorker {
	public LocalityIgnorantMainWorker(int id) {
		super(id);
	}

	@Override
	public SortingWorker createSortingWorker(int id, int size) {
		return new LocalityIgnorantSortingWorker(id, size);
	}

	@Override
	public MergingWorker createMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		return new LocalityIgnorantMergingWorker(id, left, right);
	}

	public void run() {
		super.run();
	}
}

class LocalityIgnorantSortingWorker extends SortingWorker {
	public LocalityIgnorantSortingWorker(int id, int size) {
		super(id, size);
	}

	public void run() {
		super.run();
	}
}

class LocalityIgnorantMergingWorker extends MergingWorker {
	public LocalityIgnorantMergingWorker(int id, MergeSortWorker left,
			MergeSortWorker right) {
		super(id, left, right);
	}

	public void run() {
		super.run();
	}
}

public class LocalityIgnorantCacheEfficientTest extends CacheEfficientTest {
	public LocalityIgnorantCacheEfficientTest() {
		super();
	}

	@Override
	public MainWorker createMainWorker(int id) {
		return new LocalityIgnorantMainWorker(id);
	}
}