package ch.ethz.matmult.threadpool;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public class TaskFactory {
	protected MultiplicationTask createMultiplicationTask(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		return new MultiplicationTask(this, a, b, c, quadrant);
	}

	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new AdditionTask(this, a, b, c, quadrant);
	}
}
