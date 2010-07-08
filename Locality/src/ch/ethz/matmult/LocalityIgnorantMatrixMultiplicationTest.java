package ch.ethz.matmult;

class LocalityIgnorantMultiplicationWorker extends MultiplicationWorker {
	public LocalityIgnorantMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityIgnorantMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityIgnorantAdditionWorker(a, b, c, quadrant);
	}
}

class LocalityIgnorantAdditionWorker extends AdditionWorker {
	public LocalityIgnorantAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityIgnorantMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityIgnorantAdditionWorker(a, b, c, quadrant);
	}
}

public class LocalityIgnorantMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public LocalityIgnorantMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityIgnorantMultiplicationWorker(a, b, c, quadrant);
	}
}
