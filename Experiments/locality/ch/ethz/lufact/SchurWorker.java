package ch.ethz.lufact;

public class SchurWorker extends Thread {
	private MatrixPosition posM, posV, posW;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public SchurWorker(MatrixPosition posM, MatrixPosition posV,
			MatrixPosition posW, int numOfBlocks, double[][] LU, int blockSize) {
		this.posM = posM;
		this.posV = posV;
		this.posW = posW;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		/* Check base case. */
		if (numOfBlocks == 1) {
			blockSchur(posM, posV, posW);
			return;
		}

		MatrixPosition posM00, posM01, posM10, posM11;
		MatrixPosition posV00, posV01, posV10, posV11;
		MatrixPosition posW00, posW01, posW10, posW11;
		int halfNb;

		/* Break matrices into 4 pieces. */
		halfNb = numOfBlocks / 2;
		posM00 = posM;
		posM01 = new MatrixPosition(posM.row, posM.col + (halfNb * blockSize));
		posM10 = new MatrixPosition(posM.row + (halfNb * blockSize), posM.col);
		posM11 = new MatrixPosition(posM.row + (halfNb * blockSize), posM.col
				+ (halfNb * blockSize));
		posV00 = posV;
		posV01 = new MatrixPosition(posV.row, posV.col + (halfNb * blockSize));
		posV10 = new MatrixPosition(posV.row + (halfNb * blockSize), posV.col);
		posV11 = new MatrixPosition(posV.row + (halfNb * blockSize), posV.col
				+ (halfNb * blockSize));

		posW00 = posW;
		posW01 = new MatrixPosition(posW.row, posW.col + (halfNb * blockSize));
		posW10 = new MatrixPosition(posW.row + (halfNb * blockSize), posW.col);
		posW11 = new MatrixPosition(posW.row + (halfNb * blockSize), posW.col
				+ (halfNb * blockSize));

		/* Form Schur complement with recursive calls. */
		try {
			SchurWorker schur0 = new SchurWorker(posM00, posV00, posW00,
					halfNb, LU, blockSize);
			schur0.start();
			SchurWorker schur1 = new SchurWorker(posM01, posV00, posW01,
					halfNb, LU, blockSize);
			schur1.start();
			SchurWorker schur2 = new SchurWorker(posM10, posV10, posW00,
					halfNb, LU, blockSize);
			schur2.start();
			SchurWorker schur3 = new SchurWorker(posM11, posV10, posW01,
					halfNb, LU, blockSize);
			schur3.start();

			schur0.join();
			schur1.join();
			schur2.join();
			schur3.join();

			schur0 = new SchurWorker(posM00, posV01, posW10, halfNb, LU,
					blockSize);
			schur0.start();
			schur1 = new SchurWorker(posM01, posV01, posW11, halfNb, LU,
					blockSize);
			schur1.start();
			schur2 = new SchurWorker(posM10, posV11, posW10, halfNb, LU,
					blockSize);
			schur2.start();
			schur3 = new SchurWorker(posM11, posV11, posW11, halfNb, LU,
					blockSize);
			schur3.start();

			schur0.join();
			schur1.join();
			schur2.join();
			schur3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * blockSchur - Compute Schur complement B' = B - AC.
	 * 
	 * @param posA
	 *            The start position of matrix A in array LU where size of
	 *            matrix A is blockSize by blockSize
	 * @param posB
	 *            The start position of matrix B in array LU where size of
	 *            matrix B is blockSize by blockSize
	 * @param posC
	 *            The start position of matrix C in array LU where size of
	 *            matrix C is blockSize by blockSize
	 **/
	private void blockSchur(MatrixPosition posB, MatrixPosition posA,
			MatrixPosition posC) {
		double a;
		int i, k, n;

		/* Compute Schur complement. */
		for (i = 0; i < blockSize; i++) {
			for (k = 0; k < blockSize; k++) {
				a = LU[posA.row + i][posA.col + k];
				for (n = blockSize - 1; n >= 0; n--) {
					LU[posB.row + i][posB.col + n] -= a
							* LU[posC.row + k][posC.col + n];
				}
			}
		}
	}
}
