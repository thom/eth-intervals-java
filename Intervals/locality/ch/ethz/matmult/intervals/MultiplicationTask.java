package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.EmptyInterval;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MultiplicationTask extends MatrixTask {
	private Matrix lhs, rhs;

	public MultiplicationTask(@ParentForNew("Parent") Dependency dep,
			Place place, TaskFactory factory, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(dep, place, "multiplication-task-", factory, a, b, c, quadrant);
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

			Quadrant[][] quadrants = Quadrant.matrix();

			Interval barrier = new EmptyInterval(parent, "Barrier");

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					MultiplicationTask mult0 = createMultiplicationTask(
							parent, place, aa[row][0], bb[0][col],
							ll[row][col], quadrants[row][col]);
					MultiplicationTask mult1 = createMultiplicationTask(
							parent, place, aa[row][1], bb[1][col],
							rr[row][col], quadrants[row][col]);

					Intervals.addHb(mult0, barrier);
					Intervals.addHb(mult1, barrier);
				}
			}

			// Do sum
			AdditionTask add = createAdditionTask(this, place, lhs, rhs, c,
					quadrant);
			Intervals.addHb(barrier, add);
		}
	}
}
