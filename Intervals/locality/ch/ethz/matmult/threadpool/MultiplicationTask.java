package ch.ethz.matmult.threadpool;

import java.util.concurrent.Future;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public class MultiplicationTask extends MatrixTask {
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

			Future<?>[][][] multiplicationTasks = new Future<?>[2][2][2];
			Quadrant[][] quadrants = Quadrant.matrix();

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					multiplicationTasks[row][col][0] = Benchmark.exec
							.submit(createMultiplicationTask(aa[row][0],
									bb[0][col], ll[row][col],
									quadrants[row][col]));
					multiplicationTasks[row][col][1] = Benchmark.exec
							.submit(createMultiplicationTask(aa[row][1],
									bb[1][col], rr[row][col],
									quadrants[row][col]));
				}
			}

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					try {
						multiplicationTasks[row][col][0].get();
						multiplicationTasks[row][col][1].get();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Do sum
			try {
				Benchmark.exec.submit(createAdditionTask(lhs, rhs, c, quadrant))
						.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
