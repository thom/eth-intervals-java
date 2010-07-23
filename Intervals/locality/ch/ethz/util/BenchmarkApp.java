package ch.ethz.util;

import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ch.ethz.hwloc.Units;

public abstract class BenchmarkApp {
	public static Units units = Machine.Mafushi.getUnits();
	public static int threads = units.size();

	// Command line values
	protected final CommandLineValues values;
	protected final CmdLineParser parser;

	// Locality benchmark
	protected LocalityBenchmark benchmark;

	// Name and package
	protected String name;
	protected String packageName;

	protected final Machine machine;
	protected final BenchmarkType type;
	protected final Locality locality;
	protected final int runs;
	protected final int kbest;

	protected long[] results;
	protected long result;

	@SuppressWarnings("deprecation")
	public BenchmarkApp(String[] args, CommandLineValues values, String name,
			String packageName) {
		// Parse the command line arguments and options
		this.values = values;
		parser = new CmdLineParser(values);

		// Set width of the error display area
		parser.setUsageWidth(80);

		try {
			parser.parseArgument(args);

			if (!(values.getRuns() >= values.getKbest()))
				throw new CmdLineException("runs must be >= than kbest");
		} catch (CmdLineException e) {
			System.err.println(e.getMessage() + "\n");
			System.err.printf("Usage:\n");

			// Print the list of available options
			parser.printUsage(System.err);
			System.exit(1);
		}

		machine = values.getMachine();
		units = machine.getUnits();

		type = values.getType();
		locality = values.getLocality();
		threads = values.getThreads() == 0 ? units.size() : values.getThreads();
		runs = values.getRuns();
		kbest = values.getKbest();

		this.name = name;
		this.packageName = packageName;
	}

	protected abstract void run();

	protected void printTitle(String additional) {
		System.out.printf("Benchmark: %s\n", name);

		if (type != BenchmarkType.single) {
			System.out.printf("Locality: %s\n", locality.toString()
					.toLowerCase());
		}

		System.out.printf("Machine: %s\nUnits: %s\nImplemenation: %s\n",
				machine.toString().toLowerCase(), units.size(), type.toString()
						.toLowerCase());

		if (type == BenchmarkType.threadpool) {
			System.out
					.printf("Number of threads in thread pool: %s\n", threads);
		}

		if (additional != null) {
			System.out.println(additional);
		}
	}

	protected void runBenchmark() {
		Class<?> klass;

		try {
			klass = Class
					.forName(String.format(
							"%s.%s.%s",
							packageName,
							type.toString(),
							((type == BenchmarkType.single) || (type == BenchmarkType.threadpool)) ? "Benchmark"
									: locality.toString() + "LocalityBenchmark"));
			benchmark = (LocalityBenchmark) klass.getConstructor()
					.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		results = new long[runs];
		for (int i = 0; i < runs; i++) {
			results[i] = benchmark.run();
			System.out.printf("Run %d = %d\n", i, results[i]);
		}

		Arrays.sort(results);

		result = 0;
		for (int i = 0; i < kbest; i++) {
			result += results[i];
		}
	}

	protected void printResult() {
		System.out.printf("\n%d-best = %f\n", kbest, (new Double(result))
				/ kbest);
	}
}
