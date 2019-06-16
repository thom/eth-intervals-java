package ch.ethz.matmult;

import org.kohsuke.args4j.Option;

import ch.ethz.util.BenchmarkApp;
import ch.ethz.util.CommandLineValues;

public class Main extends BenchmarkApp {
	// Must be a power of 2!
	public static int matrixDimension = 2048;
	public static int stopRecursionAt = 256;

	public static int upperBound = 100;

	public Main(String[] args, MyCommandLineValues values) {
		super(args, values, "matrix multiplication", "ch.ethz.matmult");
		matrixDimension = values.getMatrixDimension();
		stopRecursionAt = values.getStopRecursionAt();

		// Check matrixDimension
		int logSize = 1;
		while ((1 << logSize) < matrixDimension) {
			logSize++;
		}

		String errorMessage = null;
		boolean error = false;

		if (!(stopRecursionAt < matrixDimension)) {
			error = true;
			errorMessage = "stopRecursionAt must be smaller than matrixDimension";
		}

		if (Math.pow(2, logSize) != matrixDimension) {
			error = true;
			errorMessage = "matrixDimension must be power of 2";
		}

		if (error) {
			System.err.println(errorMessage + "\n");
			System.err.printf("Usage:\n");

			// Print the list of available options
			parser.printUsage(System.err);
			System.exit(1);
		}
	}

	protected void run() {
		printTitle(String
				.format("Matrix dimension: %d\nStop recursion at: %d\nUpper bound: %d\n",
						matrixDimension, stopRecursionAt, upperBound));
		runBenchmark();
		printResult();
	}

	public static void main(String[] args) throws Exception {
		Main app = new Main(args, new MyCommandLineValues());
		app.run();
	}

	protected static class MyCommandLineValues extends CommandLineValues {
		@Option(name = "-d", aliases = { "--dimension" }, usage = "matrix dimension, must be power of 2, default value: 2048")
		private int matrixDimension = 2048;

		@Option(name = "-a", aliases = { "--stop-at" }, usage = "stop recursion at, default value: 256")
		public int stopRecursionAt = 256;

		@Option(name = "-u", aliases = { "--upper-bound" }, usage = "upper bound of random array elements, default value: 100")
		private int upperBound = 100;

		public int getMatrixDimension() {
			return matrixDimension;
		}

		public int getStopRecursionAt() {
			return stopRecursionAt;
		}

		public int getUpperBound() {
			return upperBound;
		}
	}
}
