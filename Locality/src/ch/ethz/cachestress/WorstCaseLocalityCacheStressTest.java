package ch.ethz.cachestress;

import ch.ethz.hwloc.SetAffinityException;

class WorstCaseLocalityWorker extends CacheStressWorker {
	private int places;

	public WorstCaseLocalityWorker(int id, int[] sharedArray, int begin, int end) {
		super(id, sharedArray, begin, end);
		places = getPlace().getNumberOfPlaces();
	}

	public void run() {
		try {
			getPlace().set((getWorkerId() + (places / 2)) % places);
		} catch (SetAffinityException e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.run();
	}
}

public class WorstCaseLocalityCacheStressTest extends CacheStressTest {
	public WorstCaseLocalityCacheStressTest(int arraySize) {
		super(arraySize);
	}

	@Override
	public CacheStressWorker createCacheStressWorker(int id, int[] array,
			int begin, int end) {
		return new WorstCaseLocalityWorker(id, array, begin, end);
	}
}
