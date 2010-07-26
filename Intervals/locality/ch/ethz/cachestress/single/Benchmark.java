package ch.ethz.cachestress.single;

import ch.ethz.cachestress.Main;
import ch.ethz.cachestress.Task;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	private int units, tasksPerUnit;

	public Benchmark() {
		this.units = Main.places.unitsLength;
		this.tasksPerUnit = Main.tasksPerUnit;
	}

	public void doWork(int id, int[] array) {
		new Task(id, array).run();
	}

	public long run() {
		startBenchmark();

		int[] array1 = createRandomIntegerArray(Main.arraySize);
		int[] array2 = createRandomIntegerArray(Main.arraySize);

		// Create tasks
		for (int i = 0; i < tasksPerUnit * units; i++) {
			if ((i % units) < 4) {
				doWork(i, array1);
			} else {
				doWork(i, array2);
			}
		}

		return stopBenchmark();
	}
}
