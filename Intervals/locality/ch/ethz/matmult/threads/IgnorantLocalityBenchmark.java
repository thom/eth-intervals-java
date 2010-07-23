package ch.ethz.matmult.threads;

import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class IgnorantLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new IgnorantLocalityAdditionTask(a, b, c, quadrant);
	}
}

class IgnorantLocalityMultiplicationTask extends MultiplicationTask {
	public IgnorantLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new IgnorantLocalityTaskFactory(), a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}
}

class IgnorantLocalityAdditionTask extends AdditionTask {
	public IgnorantLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new IgnorantLocalityTaskFactory(), a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super(new IgnorantLocalityTaskFactory());
	}
}