package ch.ethz.cacheefficient;

import ch.ethz.util.StopWatch;

public abstract class CacheEfficientTest {
	private MainWorker[] mainWorkers;

	public abstract MainWorker createMainWorker(int id);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Start stop watch
		stopWatch.start();

		// Create main workers
		mainWorkers = new MainWorker[Config.MAIN_WORKERS];
		for (int i = 0; i < Config.MAIN_WORKERS; i++) {
			mainWorkers[i] = createMainWorker(i);
		}

		// Start workers
		for (MainWorker mw : mainWorkers) {
			mw.start();
		}

		// Wait for workers to finish
		for (MainWorker mw : mainWorkers) {
			try {
				mw.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Stop stop watch
		stopWatch.stop();

		// Check results
		Integer[] result;
		for (MainWorker mw : mainWorkers) {
			result = mw.mergingWorker.array;
			for (int i = 0; i < result.length - 1; i++) {
				if (result[i] > result[i + 1]) {
					System.out.printf("Main Worker %d: Array is not sorted!!!",
							mw.id);
					System.exit(1);
				}
			}
		}

		return stopWatch.getElapsedTime();
	}
}
