package ch.ethz.matmult.threads;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class IgnorantLocalityWorkerFactory extends WorkerFactory {
	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new IgnorantLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class IgnorantLocalityMultiplicationWorker extends MultiplicationWorker {
	public IgnorantLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new IgnorantLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityAdditionWorker extends AdditionWorker {
	public IgnorantLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new IgnorantLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super(new IgnorantLocalityWorkerFactory());
	}
}