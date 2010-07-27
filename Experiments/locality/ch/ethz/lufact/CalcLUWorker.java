package ch.ethz.lufact;

public class CalcLUWorker extends Thread {
	private MatrixPosition pos;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public CalcLUWorker(MatrixPosition pos, int numOfBlocks, double[][] LU,
			int blockSize) {
		this.pos = pos;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		// first time called
		if (pos == null) {
			pos = new MatrixPosition(0, 0);
		}

		if (numOfBlocks == 1) {
			blockLU(pos);
			return;
		}

		int halfNb = numOfBlocks / 2;
		MatrixPosition pos00, pos01, pos10, pos11;

		pos00 = pos;
		pos01 = new MatrixPosition(pos.row, pos.col + (halfNb * blockSize));
		pos10 = new MatrixPosition(pos.row + (halfNb * blockSize), pos.col);
		pos11 = new MatrixPosition(pos.row + (halfNb * blockSize), pos.col
				+ (halfNb * blockSize));

		try {
			CalcLUWorker calcLU = new CalcLUWorker(pos00, halfNb, LU, blockSize);
			calcLU.start();
			calcLU.join();

			LowerSolveWorker lowerSolve = new LowerSolveWorker(pos01, pos00,
					halfNb, LU, blockSize);
			lowerSolve.start();

			UpperSolveWorker upperSolve = new UpperSolveWorker(pos10, pos00,
					halfNb, LU, blockSize);
			upperSolve.start();

			lowerSolve.join();
			upperSolve.join();

			SchurWorker schur = new SchurWorker(pos11, pos10, pos01, halfNb,
					LU, blockSize);
			schur.start();
			schur.join();

			calcLU = new CalcLUWorker(pos11, halfNb, LU, blockSize);
			calcLU.start();
			calcLU.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * blockLU - Factor block B.
	 * 
	 * @param posB
	 *            The start position of matrix B in array LU where size of
	 *            matrix B is blockSize by blockSize
	 **/
	private void blockLU(MatrixPosition posB) {
		double a;
		int i, k, n;

		/* Factor block. */
		for (k = 0; k < blockSize; k++) {
			for (i = k + 1; i < blockSize; i++) {
				LU[posB.row + i][posB.col + k] /= LU[posB.row + k][posB.col + k];
				a = LU[posB.row + i][posB.col + k];
				for (n = blockSize - 1; n >= (k + 1); n--) {
					LU[posB.row + i][posB.col + n] -= a
							* LU[posB.row + k][posB.col + n];
				}
			}
		}
	}
}
