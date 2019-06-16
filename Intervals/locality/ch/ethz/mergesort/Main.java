package ch.ethz.mergesort;

import org.kohsuke.args4j.Option;

import ch.ethz.util.BenchmarkApp;
import ch.ethz.util.CommandLineValues;

public class Main extends BenchmarkApp {
	public static int arraySize = 2 * 2097144;
	public static int upperBound = 100;
	public static int sortersPerPlace = 8;

	public Main(String[] args, MyCommandLineValues values) {
		super(args, values, "merge sort", "ch.ethz.mergesort");
		arraySize = values.getArraySize();
		upperBound = values.getUpperBound();
		sortersPerPlace = values.getSortersPerPlace();

		if (arraySize % (sortersPerPlace * places.length) != 0) {
			System.err
					.println("Array size must be a multiple of the overall number of sorters, i.e.");
			System.err
					.println("Array size MOD (sorters per place * places) must be 0\n");
			System.err.printf("\t%d MOD (%d * %d) != 0\n\n", arraySize,
					sortersPerPlace, places.length);
			System.err.printf("Usage:\n");

			// Print the list of available options
			parser.printUsage(System.err);
			System.exit(1);
		}
	}

	protected void run() {
		printTitle(String.format(
				"Array size: %d\nUpper bound: %d\nSorters per place: %d\n",
				arraySize, upperBound, sortersPerPlace));
		runBenchmark();
		printResult();
	}

	public static void main(String[] args) throws Exception {
		Main app = new Main(args, new MyCommandLineValues());
		app.run();
	}

	protected static class MyCommandLineValues extends CommandLineValues {
		@Option(name = "-a", aliases = { "--array-size" }, usage = "array size, default value: 2 * 2097144")
		private int arraySize = 2 * 2097144;

		@Option(name = "-u", aliases = { "--upper-bound" }, usage = "upper bound of random array elements, default value: 100")
		private int upperBound = 100;

		@Option(name = "-s", aliases = { "--sorters-per-place" }, usage = "number of sorters per place, default value: 4")
		private int sortersPerPlace = 4;

		public int getArraySize() {
			return arraySize;
		}

		public int getUpperBound() {
			return upperBound;
		}

		public int getSortersPerPlace() {
			return sortersPerPlace;
		}
	}
}
