package ch.ethz.matmult.threadpool;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MatrixTask implements Runnable {
	protected final String name;
	protected final TaskFactory factory;
	protected final Matrix a, b, c;
	protected final Quadrant quadrant;

	public MatrixTask(String name, TaskFactory factory, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		this.name = name + quadrant;
		this.factory = factory;
		this.a = a;
		this.b = b;
		this.c = c;
		this.quadrant = quadrant;
	}

	protected MultiplicationTask createMultiplicationTask(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		return factory.createMultiplicationTask(a, b, c, quadrant);
	}

	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return factory.createAdditionTask(a, b, c, quadrant);
	}
}
