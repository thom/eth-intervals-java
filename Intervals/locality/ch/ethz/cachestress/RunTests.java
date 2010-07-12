package ch.ethz.cachestress;

import java.util.Arrays;

import ch.ethz.cachestress.threads.BestLocalityBenchmark;
import ch.ethz.util.LocalityBenchmark;
import ch.ethz.util.TestType;

enum Locality {
	BEST, IGNORANT, WORST, RANDOM
}

public class RunTests {
	public static void main(String[] args) throws Exception {
		Locality locality = Locality.BEST;
		TestType type = TestType.THREADS;

		if (args.length > 0) {
			try {
				locality = Locality.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("No such locality'" + args[0] + "'");
				System.exit(1);
			}
		}

		if (args.length > 1) {
			try {
				type = TestType.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("No such test type '" + args[0] + "'");
				System.exit(1);
			}
		}

		System.out.printf(
				"Running %s test with %s locality (%s implementation)\n\n",
				Config.name, locality.toString().toLowerCase(), type.toString()
						.toLowerCase());

		LocalityBenchmark benchmark;
		String packageName = Config.packageName + "."
				+ type.toString().toLowerCase() + ".";
		Class<?> klass = null;

		switch (locality) {
		case BEST:
			klass = Class.forName(packageName + "BestLocalityBenchmark");
			break;
		case IGNORANT:
			klass = Class.forName(packageName + "IgnorantLocalityBenchmark");
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

		benchmark = (LocalityBenchmark) klass.getConstructor().newInstance();

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
