package ch.ethz.mergesort;

import org.kohsuke.args4j.Option;

import ch.ethz.util.BenchmarkApp;
import ch.ethz.util.CommandLineValues;

public class Main extends BenchmarkApp {
	public static int arraySize = 2 * 2097144;
	public static int upperBound = 100;

	public Main(String[] args, MyCommandLineValues values) {
		super(args, values, "merge sort", "ch.ethz.mergesort");
		arraySize = values.getArraySize();
	}

	protected void run() {
		printTitle(String.format("Array size: %d\nUpper bound: %d\n",
				arraySize, upperBound));
		runBenchmark();
		printResult();
	}

	public static void main(String[] args) throws Exception {
		Main app = new Main(args, new MyCommandLineValues());
		app.run();
	}

	protected static class MyCommandLineValues extends CommandLineValues {
		@Option(name = "-a", aliases = { "--array-size" }, usage = "array size (default: 2 * 2097144)")
		private int arraySize = 2 * 2097144;

		@Option(name = "-u", aliases = { "--upper-bound" }, usage = "upper bound of random array elements (default: 100)")
		private int upperBound = 100;

		public int getArraySize() {
			return arraySize;
		}

		public int getUpperBound() {
			return upperBound;
		}
	}
}
