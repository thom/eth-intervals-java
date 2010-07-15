package ch.ethz.mergesort.single;

import ch.ethz.mergesort.Main;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	private int numberOfSorters;

	public Benchmark() {
		this.numberOfSorters = Main.units.size() * Main.sortersPerUnit;
	}

	public long run() {
		startBenchmark();

		// TODO: Fill work

		return stopBenchmark();
	}
}
