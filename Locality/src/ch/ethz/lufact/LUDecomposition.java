package ch.ethz.lufact;

public class LUDecomposition {
	/**
	 * Array for internal storage of decomposition.
	 * 
	 * @serial internal array storage.
	 **/
	private double[][] LU;

	/**
	 * The dimension of the array (must be square)
	 **/
	private int size;

	/**
	 * The block size
	 */
	private int blockSize;

	private static final double MAX_DIFF_THRESHOLD = 0.00001;

	/**
	 * LU Decomposition
	 * 
	 * @param A
	 *            Square matrix
	 **/
	public LUDecomposition(Matrix A, int blockSize) {
		LU = A.getArrayCopy();
		size = A.getRowDimension();
		if (size != A.getColumnDimension()) {
			throw new RuntimeException("The matrix must be a square matrix");
		}
		this.blockSize = blockSize;
	}

	public void run() {
		int numOfBlocks = size / blockSize;

		try {
			CalcLUWorker calcLU = new CalcLUWorker(null, numOfBlocks, LU,
					blockSize);
			calcLU.start();
			calcLU.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Matrix getLU() {
		return new Matrix(LU);
	}

	/**
	 * Return upper triangular factor
	 * 
	 * @return L
	 **/
	public Matrix getL() {
		Matrix X = new Matrix(size, size);
		double[][] L = X.getArray();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i > j) {
					L[i][j] = LU[i][j];
				} else if (i == j) {
					L[i][j] = 1.0;
				} else {
					L[i][j] = 0.0;
				}
			}
		}

		return X;
	}

	/**
	 * Return upper triangular factor
	 * 
	 * @return U
	 **/
	public Matrix getU() {
		Matrix X = new Matrix(size, size);
		double[][] U = X.getArray();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i <= j) {
					U[i][j] = LU[i][j];
				} else {
					U[i][j] = 0.0;
				}
			}
		}

		return X;
	}

	/**
	 * verifyResult - Check that matrix LU contains LU decomposition of A.
	 * 
	 * @return true if LU contains LU decomposition of A; otherwise false.
	 */
	public boolean verifyResult(Matrix A) {
		double[][] a;
		double diff, maxDiff, v;
		int i, j, k;

		maxDiff = 0.0d;

		a = A.getArray();

		if (LU.length != A.getRowDimension()
				|| LU[0].length != A.getColumnDimension()) {
			return false;
		}

		// Find maximum difference between any element of LU and M
		for (i = 0; i < LU.length; i++) {
			for (j = 0; j < LU.length; j++) {
				v = 0.0;
				for (k = 0; k < i && k <= j; k++) {
					v += LU[i][k] * LU[k][j];
				}
				if (k == i && k <= j) {
					v += LU[k][j];
				}
				diff = Math.abs(a[i][j] - v);
				if (diff > maxDiff)
					maxDiff = diff;
			}
		}

		if (maxDiff <= MAX_DIFF_THRESHOLD) {
			return true;
		} else {
			System.out.println("Bad Result: maxDiff is " + maxDiff);
			return false;
		}

	}
}