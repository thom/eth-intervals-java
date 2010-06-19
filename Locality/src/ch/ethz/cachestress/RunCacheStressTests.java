package ch.ethz.cachestress;

import java.util.Arrays;

public class RunCacheStressTests {
	public static final int ARRAY_SIZE = 4194296;
	public static final int RUNS = 10;
	public static final int K_BEST = 3;

	public enum TestType {
		locality, ignorant, random, worstcase
	}

	public static void main(String[] args) {
		TestType type = TestType.locality;

		if (args.length > 0) {
			try {
				type = TestType.valueOf(args[0]);
			} catch (IllegalArgumentException e) {
				System.out.println("No such test type '" + args[0] + "'");
				System.exit(1);
			}
		}

		CacheStressTest test;

		switch (type) {
		case locality:
			test = new LocalityAwareCacheStressTest(ARRAY_SIZE);
			break;
		// TODO: Run other tests!
		default:
			test = new LocalityAwareCacheStressTest(ARRAY_SIZE);
		}

		long results[] = new long[10];
		for (int i = 0; i < RUNS; i++) {
			results[i] = test.run();
			System.out.println(results[i]);
		}

		Arrays.sort(results);

		long result = 0;
		for (int i = 0; i < K_BEST; i++) {
			result += results[i];
		}

		System.out.println(result / K_BEST);
	}
}
