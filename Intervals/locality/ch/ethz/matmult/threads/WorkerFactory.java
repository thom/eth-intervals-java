package ch.ethz.matmult.threads;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class WorkerFactory {
	protected abstract MultiplicationWorker createMultiplicationWorker(
			Matrix a, Matrix b, Matrix c, Quadrant quadrant);

	protected abstract AdditionWorker createAdditionWorker(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant);
}
