package ch.ethz.cachestress;

import org.kohsuke.args4j.Option;

import ch.ethz.util.BenchmarkApp;
import ch.ethz.util.CommandLineValues;

public class Main extends BenchmarkApp {
	public static int arraySize = 2097144;
	public static int rounds = 2;

	public Main(String[] args, MyCommandLineValues values) {
		super(args, values, "cache stress", "ch.ethz.cachestress");
		arraySize = values.getArraySize();
		rounds = values.getRounds();
	}

	protected void run() {
		printTitle(String.format("Array size: %d\nRounds per core: %d\n",
				arraySize, rounds));
		runBenchmark();
		printResult();
	}

	public static void main(String[] args) throws Exception {
		Main app = new Main(args, new MyCommandLineValues());
		app.run();
	}

	protected static class MyCommandLineValues extends CommandLineValues {
		@Option(name = "-a", aliases = { "--array-size" }, usage = "array size, default value: 2097144")
		private int arraySize = 2097144;

		@Option(name = "-c", aliases = { "--rounds" }, usage = "rounds of work per core, default value: 2")
		private int rounds = 2;

		public int getArraySize() {
			return arraySize;
		}

		public int getRounds() {
			return rounds;
		}
	}
}
