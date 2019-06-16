package ch.ethz.matmult.threads;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class TaskFactory {
	protected abstract MultiplicationTask createMultiplicationTask(
			Matrix a, Matrix b, Matrix c, Quadrant quadrant);

	protected abstract AdditionTask createAdditionTask(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant);
}
