package ch.ethz.matmult;

public abstract class MatrixWorker extends Thread {
	protected final Matrix a, b, c;
	protected final Quadrant quadrant;

	public MatrixWorker(String name, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(name + quadrant);
		this.a = a;
		this.b = b;
		this.c = c;
		this.quadrant = quadrant;
	}

	protected abstract MultiplicationWorker createMultiplicationWorker(
			Matrix a, Matrix b, Matrix c, Quadrant quadrant);

	protected abstract AdditionWorker createAdditionWorker(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant);
}
