package ch.ethz.mergesort;

import org.kohsuke.args4j.Option;

import ch.ethz.util.BenchmarkApp;
import ch.ethz.util.CommandLineValues;

public class Main extends BenchmarkApp {
	public static int arraySize = 2 * 2097144;
	public static int upperBound = 100;
	public static int sortersPerUnit = 2;

	public Main(String[] args, MyCommandLineValues values) {
		super(args, values, "merge sort", "ch.ethz.mergesort");
		arraySize = values.getArraySize();
		upperBound = values.getUpperBound();
		sortersPerUnit = values.getSortersPerUnit();

		if (arraySize % (sortersPerUnit * units.size()) != 0) {
			System.err
					.println("Array size must be a multiple of the overall number of sorters, i.e.");
			System.err
					.println("Array size MOD (sorters per unit * units) must be 0\n");
			System.err.printf("\t%d MOD (%d * %d) != 0\n\n", arraySize,
					sortersPerUnit, units.size());
			System.err.printf("Usage:\n");

			// Print the list of available options
			parser.printUsage(System.err);
			System.exit(1);
		}
	}

	protected void run() {
		printTitle(String.format(
				"Array size: %d\nUpper bound: %d\nSorters per unit: %d\n",
				arraySize, upperBound, sortersPerUnit));
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

		@Option(name = "-s", aliases = { "--sorters-per-unit" }, usage = "number of sorters per unit, default value: 2")
		private int sortersPerUnit = 2;

		public int getArraySize() {
			return arraySize;
		}

		public int getUpperBound() {
			return upperBound;
		}

		public int getSortersPerUnit() {
			return sortersPerUnit;
		}
	}
}
