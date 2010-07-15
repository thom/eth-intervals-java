package ch.ethz.cachestress.single;

import ch.ethz.cachestress.Main;
import ch.ethz.cachestress.Worker;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	private int units, workersPerUnit;

	public Benchmark() {
		this.units = Main.units.size();
		this.workersPerUnit = Main.workersPerUnit;
	}

	public void doWork(int id, int[] array) {
		new Worker(id, array).run();
	}

	public long run() {
		startBenchmark();

		int[] array1 = createRandomIntegerArray(Main.arraySize);
		int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create workers
		for (int i = 0; i < workersPerUnit * units; i++) {
			if ((i % units) < 4) {
				doWork(i, array1);
			} else {
				doWork(i, array2);
			}
		}

		return stopBenchmark();
	}
}
