package ch.ethz.lufact;

public class UpperSolveWorker extends Thread {
	private MatrixPosition posM, posU;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public UpperSolveWorker(MatrixPosition posM, MatrixPosition posU,
			int numOfBlocks, double[][] LU, int blockSize) {
		this.posM = posM;
		this.posU = posU;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		/* Check base case. */
		if (numOfBlocks == 1) {
			blockUpperSolve(posM, posU);
			return;
		}

		/* Break matrices into 4 pieces. */
		int halfNb = numOfBlocks / 2;
		MatrixPosition posM00, posM01, posM10, posM11;

		posM00 = posM;
		posM01 = new MatrixPosition(posM.row, posM.col + (halfNb * blockSize));
		posM10 = new MatrixPosition(posM.row + (halfNb * blockSize), posM.col);
		posM11 = new MatrixPosition(posM.row + (halfNb * blockSize), posM.col
				+ (halfNb * blockSize));

		/* Solve with recursive calls. */
		try {
			AuxUpperSolveWorker auxUpperSolve1 = new AuxUpperSolveWorker(
					posM00, posM01, posU, halfNb, LU, blockSize);
			auxUpperSolve1.start();
			AuxUpperSolveWorker auxUpperSolve2 = new AuxUpperSolveWorker(
					posM10, posM11, posU, halfNb, LU, blockSize);
			auxUpperSolve2.start();
			auxUpperSolve1.join();
			auxUpperSolve2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * blockUpperSolve - Perform forward substitution to solve for B' in B'U =
	 * B.
	 * 
	 * @param posB
	 *            The start position of matrix B in array LU where size of
	 *            matrix B is blockSize by blockSize
	 * @param posU
	 *            The start position of matrix U in array LU where size of
	 *            matrix U is blockSize by blockSize
	 **/
	private void blockUpperSolve(MatrixPosition posB, MatrixPosition posU) {
		double a;
		int i, k, n;

		/* Perform forward substitution. */
		for (i = 0; i < blockSize; i++) {
			for (k = 0; k < blockSize; k++) {
				LU[posB.row + i][posB.col + k] /= LU[posU.row + k][posU.col + k];
				a = LU[posB.row + i][posB.col + k];
				for (n = blockSize - 1; n >= (k + 1); n--) {
					LU[posB.row + i][posB.col + n] -= a
							* LU[posU.row + k][posU.col + n];
				}
			}
		}
	}
}
