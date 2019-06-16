package ch.ethz.matmult.threads;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MultiplicationTask extends MatrixTask {
	private Matrix lhs, rhs;

	public MultiplicationTask(TaskFactory factory, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super("multiplication-task-", factory, a, b, c, quadrant);
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

			MultiplicationTask[][][] multiplicationTasks = new MultiplicationTask[2][2][2];
			Quadrant[][] quadrants = Quadrant.matrix();

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					multiplicationTasks[row][col][0] = createMultiplicationTask(
							aa[row][0], bb[0][col], ll[row][col],
							quadrants[row][col]);
					multiplicationTasks[row][col][1] = createMultiplicationTask(
							aa[row][1], bb[1][col], rr[row][col],
							quadrants[row][col]);
					multiplicationTasks[row][col][0].start();
					multiplicationTasks[row][col][1].start();
				}
			}

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					try {
						multiplicationTasks[row][col][0].join();
						multiplicationTasks[row][col][1].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// Do sum
			AdditionTask additionTask = createAdditionTask(lhs, rhs, c,
					quadrant);
			additionTask.start();
			try {
				additionTask.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
