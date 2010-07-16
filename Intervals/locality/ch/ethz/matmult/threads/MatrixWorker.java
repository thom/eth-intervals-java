package ch.ethz.matmult.threads;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MatrixWorker extends Thread {
	protected final WorkerFactory factory;
	protected final Matrix a, b, c;
	protected final Quadrant quadrant;

	public MatrixWorker(String name, WorkerFactory factory, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super(name + quadrant);
		this.factory = factory;
		this.a = a;
		this.b = b;
		this.c = c;
		this.quadrant = quadrant;
	}

	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return factory.createMultiplicationWorker(a, b, c, quadrant);
	}

	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return factory.createAdditionWorker(a, b, c, quadrant);
	}
}
