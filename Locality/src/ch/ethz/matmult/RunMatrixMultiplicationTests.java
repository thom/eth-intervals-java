package ch.ethz.matmult;

import java.util.Arrays;

import ch.ethz.hwloc.*;

enum Quadrant {
	Quadrant0, Quadrant1, Quadrant2, Quadrant3, None
}

class Config {
	public static Units units = new MafushiUnits();

	// Must be a power of 2!
	public static final int MATRIX_DIMENSION = 16;
	public static final int STOP_RECURSION = 4;

	public static final int UPPER_BOUND = 100;
	public static final int RUNS = 10;
	public static final int K_BEST = 3;
}

public class RunMatrixMultiplicationTests {
	public enum TestType {
		locality, ignorant, worstcase, random
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

		MatrixMultiplicationTest test;

		switch (type) {
		// case locality:
		// test = new LocalityAwareMatrixMultiplicationTest();
		// break;
		case ignorant:
			test = new LocalityIgnorantMatrixMultiplicationTest();
			break;
		// case worstcase:
		// test = new WorstCaseLocalityMatrixMultiplicationTest();
		// break;
		// case random:
		// test = new RandomLocalityMatrixMultiplicationTest();
		// break;
		default:
			// test = new LocalityAwareMatrixMultiplicationTest();
			test = new LocalityIgnorantMatrixMultiplicationTest();
		}

		long results[] = new long[10];
		for (int i = 0; i < Config.RUNS; i++) {
			results[i] = test.run();
			System.out.printf("Run %d = %d\n", i, results[i]);
		}

		Arrays.sort(results);

		long result = 0;
		for (int i = 0; i < Config.K_BEST; i++) {
			result += results[i];
		}

		System.out.printf("%d-best = %f\n", Config.K_BEST, (new Double(result))
				/ Config.K_BEST);
	}
}
