package ch.ethz.matmult;

public abstract class MultiplicationWorker extends MatrixWorker {
	private Matrix lhs, rhs;

	public MultiplicationWorker(Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super("multiplication-worker-", a, b, c, quadrant);
		this.lhs = new Matrix(a.getDim());
		this.rhs = new Matrix(a.getDim());
	}

	public void run() {
		// TODO!!!
	}
}
