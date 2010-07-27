package ch.ethz.lufact;

public class AuxLowerSolveWorker extends Thread {
	private MatrixPosition posMa, posMb, posL;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public AuxLowerSolveWorker(MatrixPosition posMa, MatrixPosition posMb,
			MatrixPosition posL, int numOfBlocks, double[][] LU, int blockSize) {
		this.posMa = posMa;
		this.posMb = posMb;
		this.posL = posL;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		@SuppressWarnings("unused")
		MatrixPosition posL00, posL01, posL10, posL11;

		/* Break L matrix into 4 pieces. */
		posL00 = posL;
		posL01 = new MatrixPosition(posL.row, posL.col
				+ (numOfBlocks * blockSize));
		posL10 = new MatrixPosition(posL.row + (numOfBlocks * blockSize),
				posL.col);
		posL11 = new MatrixPosition(posL.row + (numOfBlocks * blockSize),
				posL.col + (numOfBlocks * blockSize));

		/* Solve with recursive calls. */
		try {
			LowerSolveWorker lowerSolve = new LowerSolveWorker(posMa, posL00,
					numOfBlocks, LU, blockSize);
			lowerSolve.start();
			lowerSolve.join();

			SchurWorker schur = new SchurWorker(posMb, posL10, posMa,
					numOfBlocks, LU, blockSize);
			schur.start();
			schur.join();

			lowerSolve = new LowerSolveWorker(posMb, posL11, numOfBlocks, LU,
					blockSize);
			lowerSolve.start();
			lowerSolve.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}
}
