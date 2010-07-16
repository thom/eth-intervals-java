package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class AdditionTask extends MatrixTask {
	public AdditionTask(@ParentForNew("Parent") Dependency dep, Place place,
			TaskFactory factory, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, "addition-worker-", factory, a, b, c, quadrant);
	}

	public void run() {
		if (a.getDim() <= Main.stopRecursionAt) {
			// c.set(0, 0, a.get(0, 0) + b.get(0, 0));
			c.add(a, b);
		} else {
			Matrix[][] aa = a.split(), bb = b.split(), cc = c.split();

			AdditionTask[][] additionWorkers = new AdditionTask[2][2];

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					additionWorkers[row][col] = createAdditionWorker(parent,
							place, aa[row][col], bb[row][col], cc[row][col],
							quadrant);
					additionWorkers[row][col].start();
				}
			}

			// for (int row = 0; row < 2; row++) {
			// for (int col = 0; col < 2; col++) {
			// try {
			// additionWorkers[row][col].join();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			// }
		}
	}
}