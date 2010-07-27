package ch.ethz.cacheefficient;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareMainWorker extends MainWorker {
	public LocalityAwareMainWorker(int id) {
		super(id);
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

	public void run() {
		super.run();
	}
}

class LocalityAwareSortingWorker extends SortingWorker {
	public LocalityAwareSortingWorker(int id, int size) {
		super(id, size);
	}

	public void run() {
		try {
			Affinity.set(Config.units.getNode(id));
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
		super.run();
	}
}

public class LocalityAwareCacheEfficientTest extends CacheEfficientTest {
	public LocalityAwareCacheEfficientTest() {
		super();
	}

	@Override
	public MainWorker createMainWorker(int id) {
		return new LocalityAwareMainWorker(id);
	}
}
