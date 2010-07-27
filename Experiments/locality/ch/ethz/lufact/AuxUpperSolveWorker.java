package ch.ethz.lufact;

public class AuxUpperSolveWorker extends Thread {
	private MatrixPosition posMa, posMb, posU;
	private double[][] LU;
	private int numOfBlocks;
	private int blockSize;

	public AuxUpperSolveWorker(MatrixPosition posMa, MatrixPosition posMb,
			MatrixPosition posU, int numOfBlocks, double[][] LU, int blockSize) {
		this.posMa = posMa;
		this.posMb = posMb;
		this.posU = posU;
		this.LU = LU;
		this.numOfBlocks = numOfBlocks;
		this.blockSize = blockSize;
	}

	public void run() {
		@SuppressWarnings("unused")
		MatrixPosition posU00, posU01, posU10, posU11;

		/* Break U matrix into 4 pieces. */
		posU00 = posU;
		posU01 = new MatrixPosition(posU.row, posU.col
				+ (numOfBlocks * blockSize));
		posU10 = new MatrixPosition(posU.row + (numOfBlocks * blockSize),
				posU.col);
		posU11 = new MatrixPosition(posU.row + (numOfBlocks * blockSize),
				posU.col + (numOfBlocks * blockSize));

		/* Solve with recursive calls. */
		try {
			UpperSolveWorker upperSolve = new UpperSolveWorker(posMa, posU00,
					numOfBlocks, LU, blockSize);
			upperSolve.start();
			upperSolve.join();

			SchurWorker schur = new SchurWorker(posMb, posMa, posU01,
					numOfBlocks, LU, blockSize);
			schur.start();
			schur.join();

			upperSolve = new UpperSolveWorker(posMb, posU11, numOfBlocks, LU,
					blockSize);
			upperSolve.start();
			upperSolve.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}
}
