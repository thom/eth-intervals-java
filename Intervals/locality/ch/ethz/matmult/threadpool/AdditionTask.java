package ch.ethz.matmult.threadpool;

import java.util.concurrent.Future;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public class AdditionTask extends MatrixTask {
	public AdditionTask(TaskFactory factory, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super("addition-task-", factory, a, b, c, quadrant);
	}

	public void run() {
		if (a.getDim() <= Main.stopRecursionAt) {
			// c.set(0, 0, a.get(0, 0) + b.get(0, 0));
			c.add(a, b);
		} else {
			Matrix[][] aa = a.split(), bb = b.split(), cc = c.split();

			Future<?>[][] additionTasks = new Future<?>[2][2];

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					additionTasks[row][col] = Benchmark.exec
							.submit(createAdditionTask(aa[row][col],
									bb[row][col], cc[row][col], quadrant));
				}
			}

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					try {
						additionTasks[row][col].get();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}