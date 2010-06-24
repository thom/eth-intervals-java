package ch.ethz.mergesort;

import java.util.Arrays;

import ch.ethz.hwloc.*;

class Config {
	public static Units units = new MarvinUnits();
}

public class RunMergeSortTests {
	public static final int ARRAY_SIZE = 4194296;
	public static final int UPPER_BOUND = 100;
	public static final int RUNS = 10;
	public static final int K_BEST = 3;

	public enum TestType {
		locality, ignorant
		// , worstcase
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

		MergeSortTest test;

		switch (type) {
		case locality:
			test = new LocalityAwareMergeSortTest(ARRAY_SIZE, UPPER_BOUND);
			break;
		case ignorant:
			test = new LocalityIgnorantMergeSortTest(ARRAY_SIZE, UPPER_BOUND);
			break;
		// case worstcase:
		// test = new WorstCaseLocalityMergeSortTest(ARRAY_SIZE, UPPER_BOUND);
		// break;
		default:
			test = new LocalityAwareMergeSortTest(ARRAY_SIZE, UPPER_BOUND);
		}

		long results[] = new long[10];
		for (int i = 0; i < RUNS; i++) {
			results[i] = test.run();
			System.out.printf("Run %d = %d\n", i, results[i]);
		}

		Arrays.sort(results);

		long result = 0;
		for (int i = 0; i < K_BEST; i++) {
			result += results[i];
		}

		System.out.printf("%d-best = %f\n", K_BEST, (new Double(result))
				/ K_BEST);
	}
}
