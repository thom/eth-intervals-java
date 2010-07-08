package ch.ethz.matmult;

public abstract class MultiplicationWorker extends MatrixWorker {
	public MultiplicationWorker(int id, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super("multiplication-worker-", id, a, b, c, quadrant);
	}

	public void run() {
		// TODO!!!
	}

	protected abstract AdditionWorker createAdditionWorker(int id, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant);
}
