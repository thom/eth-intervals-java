package ch.ethz.matmult;

public abstract class MatrixWorker extends Thread {
	protected final int id;
	protected final Matrix a, b, c;
	protected final Quadrant quadrant;

	public MatrixWorker(String name, int id, Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super(name + id);
		this.id = id;
		this.a = a;
		this.b = b;
		this.c = c;
		this.quadrant = quadrant;
	}
}
