package ch.ethz.lufact;

public class Main {
	private static final int BLOCK_SIZE = 16;
	public static final int DEFAULT_SIZE = 16 * BLOCK_SIZE;

	public static void main(String[] args) {
		int size = 0, logSize = 0;

		if (args.length == 0) {
			size = DEFAULT_SIZE;
		} else {
			size = Integer.parseInt(args[0]);
		}
		while ((1 << logSize) < size) {
			logSize++;
		}

		if (size < BLOCK_SIZE || Math.pow(2, logSize) != size) {
			System.out.println("Usage: java LUDecomposition N");
			System.out.println("Decompose NxN matrix, where N is "
					+ "at least " + BLOCK_SIZE + " and power of 2.");
			System.out.println("Default: N = " + DEFAULT_SIZE);
			return;
		}

		Matrix A = Matrix.random(size, size);

		LUDecomposition lu = new LUDecomposition(A, BLOCK_SIZE);
		lu.run();

		boolean isLU = lu.verifyResult(A);
		System.out.println("Solution: " + isLU);
	}
}