package ch.ethz.matmult.threads;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MultiplicationWorker extends MatrixWorker {
	private Matrix lhs, rhs;

	public MultiplicationWorker(Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super("multiplication-worker-", a, b, c, quadrant);
		this.lhs = new Matrix(a.getDim());
		this.rhs = new Matrix(a.getDim());
	}

	public void run() {
		if (a.getDim() <= Main.stopRecursionAt) {
			// c.set(0, 0, a.get(0, 0) * b.get(0, 0));
			c.multiply(a, b);
		} else {
			Matrix[][] aa = a.split(), bb = b.split();
			Matrix[][] ll = lhs.split(), rr = rhs.split();

			MultiplicationWorker[][][] multiplicationWorkers = new MultiplicationWorker[2][2][2];
			Quadrant[][] quadrants = Quadrant.matrix();

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					multiplicationWorkers[row][col][0] = createMultiplicationWorker(
							aa[row][0], bb[0][col], ll[row][col],
							quadrants[row][col]);
					multiplicationWorkers[row][col][1] = createMultiplicationWorker(
							aa[row][1], bb[1][col], rr[row][col],
							quadrants[row][col]);
					multiplicationWorkers[row][col][0].start();
					multiplicationWorkers[row][col][1].start();
				}
			}

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					try {
						multiplicationWorkers[row][col][0].join();
						multiplicationWorkers[row][col][1].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// Do sum
			AdditionWorker additionWorker = createAdditionWorker(lhs, rhs, c,
					quadrant);
			additionWorker.start();
			try {
				additionWorker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
