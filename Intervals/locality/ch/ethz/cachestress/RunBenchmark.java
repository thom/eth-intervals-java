package ch.ethz.cachestress;

import java.util.Arrays;

import ch.ethz.cachestress.threads.BestLocalityBenchmark;
import ch.ethz.util.LocalityBenchmark;
import ch.ethz.util.BenchmarkType;

enum Locality {
	BEST, IGNORANT, WORST, RANDOM
}

public class RunBenchmark {
	public static void main(String[] args) throws Exception {
		BenchmarkType type = BenchmarkType.THREADS;
		Locality locality = Locality.BEST;

		if (args.length > 0) {
			try {
				type = BenchmarkType.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("No such benchmark type '" + args[0] + "'");
				System.exit(1);
			}
		}

		if (args.length > 1) {
			try {
				locality = Locality.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("No such locality'" + args[0] + "'");
				System.exit(1);
			}
		}

		LocalityBenchmark benchmark;

		if (type != BenchmarkType.SINGLE) {
			System.out
					.printf("Running %s benchmark with %s locality (%s implementation)\n\n",
							Config.name, locality.toString().toLowerCase(),
							type.toString().toLowerCase());

			String packageName = Config.packageName + "."
					+ type.toString().toLowerCase() + ".";
			Class<?> klass = null;

			switch (locality) {
			case BEST:
				klass = Class.forName(packageName + "BestLocalityBenchmark");
				break;
			case IGNORANT:
				klass = Class
						.forName(packageName + "IgnorantLocalityBenchmark");
				break;
			case WORST:
				klass = Class.forName(packageName + "WorstLocalityBenchmark");
				break;
			case RANDOM:
				klass = Class.forName(packageName + "RandomLocalityBenchmark");
				break;
			default:
				benchmark = new BestLocalityBenchmark();
			}

			benchmark = (LocalityBenchmark) klass.getConstructor()
					.newInstance();
		} else {
			System.out.printf("Running %s benchmark (%s implementation)\n\n",
					Config.name, type.toString().toLowerCase());

			benchmark = new ch.ethz.cachestress.single.Benchmark();
		}

		long results[] = new long[10];
		for (int i = 0; i < Config.RUNS; i++) {
			results[i] = benchmark.run();
			System.out.printf("Run %d = %d\n", i, results[i]);
		}

		Arrays.sort(results);

		long result = 0;
		for (int i = 0; i < Config.K_BEST; i++) {
			result += results[i];
		}

		System.out.printf("\n%d-best = %f\n", Config.K_BEST,
				(new Double(result)) / Config.K_BEST);
	}
}
