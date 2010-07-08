package ch.ethz.matmult;

class LocalityIgnorantMultiplicationWorker extends MultiplicationWorker {
	public LocalityIgnorantMultiplicationWorker(int id, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super(id, a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}

	@Override
	protected AdditionWorker createAdditionWorker(int id, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		return new LocalityIgnorantAdditionWorker(id, a, b, c, quadrant);
	}
}

class LocalityIgnorantAdditionWorker extends AdditionWorker {
	public LocalityIgnorantAdditionWorker(int id, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(id, a, b, c, quadrant);
	}

	public void run() {
		super.run();
	}
}

public class LocalityIgnorantMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public LocalityIgnorantMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(int id, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityIgnorantMultiplicationWorker(id, a, b, c, quadrant);
	}
}
