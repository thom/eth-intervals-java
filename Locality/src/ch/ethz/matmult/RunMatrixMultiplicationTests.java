package ch.ethz.matmult;

import java.util.Arrays;

import ch.ethz.hwloc.*;

enum Quadrant {
	Quadrant0, Quadrant1, Quadrant2, Quadrant3, None;

	public static Quadrant[][] matrix() {
		Quadrant[][] result = new Quadrant[2][2];
		result[0][0] = Quadrant0;
		result[0][1] = Quadrant1;
		result[1][0] = Quadrant2;
		result[1][1] = Quadrant3;
		return result;
	}
}

class Config {
	public static Units units = new MafushiUnits();

	// Must be a power of 2!
	public static final int MATRIX_DIMENSION = 2048;
	public static final int STOP_RECURSION = 256;

	public static final int UPPER_BOUND = 100;
	public static final int RUNS = 10;
	public static final int K_BEST = 3;
}

public class RunMatrixMultiplicationTests {
	public enum TestType {
		locality_row, locality_col, locality_diag, ignorant, worstcase, random_node, random_core
	}

	public static void main(String[] args) {
		TestType type = TestType.locality_row;

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
		case locality_row:
			test = new LocalityAwareRowMatrixMultiplicationTest();
			break;
		case locality_col:
			test = new LocalityAwareColMatrixMultiplicationTest();
			break;
		case locality_diag:
			test = new LocalityAwareDiagMatrixMultiplicationTest();
			break;
		case ignorant:
			test = new LocalityIgnorantMatrixMultiplicationTest();
			break;
		// case worstcase:
		// test = new WorstCaseLocalityMatrixMultiplicationTest();
		// break;
		case random_node:
			test = new RandomNodeLocalityMatrixMultiplicationTest();
			break;
		case random_core:
			test = new RandomCoreLocalityMatrixMultiplicationTest();
			break;
		default:
			test = new LocalityAwareRowMatrixMultiplicationTest();
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
