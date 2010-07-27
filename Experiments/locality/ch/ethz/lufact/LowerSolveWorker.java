package ch.ethz.lufact;

public class LowerSolveWorker extends Thread {
	private MatrixPosition posM, posL;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public LowerSolveWorker(MatrixPosition posM, MatrixPosition posL,
			int numOfBlocks, double[][] LU, int blockSize) {
		this.posM = posM;
		this.posL = posL;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		/* Check base case. */
		if (numOfBlocks == 1) {
			blockLowerSolve(posM, posL);
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
			AuxLowerSolveWorker auxLowerSolve1 = new AuxLowerSolveWorker(
					posM00, posM10, posL, halfNb, LU, blockSize);
			auxLowerSolve1.start();
			AuxLowerSolveWorker auxLowerSolve2 = new AuxLowerSolveWorker(
					posM01, posM11, posL, halfNb, LU, blockSize);
			auxLowerSolve2.start();
			auxLowerSolve1.join();
			auxLowerSolve2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * blockLowerSolve - Perform forward substitution to solve for B' in LB' =
	 * B.
	 * 
	 * @param posB
	 *            The start position of matrix B in array LU where size of
	 *            matrix B is blockSize by blockSize
	 * @param posL
	 *            The start position of matrix L in array LU where size of
	 *            matrix L is blockSize by blockSize
	 **/
	private void blockLowerSolve(MatrixPosition posB, MatrixPosition posL) {
		double a;
		int i, k, n;

		/* Perform forward substitution. */
		for (i = 1; i < blockSize; i++) {
			for (k = 0; k < i; k++) {
				a = LU[posL.row + i][posL.col + k];
				for (n = blockSize - 1; n >= 0; n--) {
					LU[posB.row + i][posB.col + n] -= a
							* LU[posB.row + k][posB.col + n];
				}
			}
		}
	}
}
