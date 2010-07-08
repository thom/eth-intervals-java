package ch.ethz.matmult;

public abstract class AdditionWorker extends MatrixWorker {
	public AdditionWorker(Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super("multiplication-worker-", a, b, c, quadrant);
	}

	public void run() {
		// TODO!!!
	}
}