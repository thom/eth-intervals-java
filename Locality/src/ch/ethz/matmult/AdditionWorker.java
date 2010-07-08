package ch.ethz.matmult;

public abstract class AdditionWorker extends MatrixWorker {
	public AdditionWorker(int id, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super("multiplication-worker-", id, a, b, c, quadrant);
	}

	public void run() {
		// TODO!!!
	}
}