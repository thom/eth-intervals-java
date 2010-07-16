package ch.ethz.matmult.single;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	public long run() {
		startBenchmark();

		// Create matrices
		Matrix a = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix b = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix c = new Matrix(Main.matrixDimension);

		// Multiply
		multiply(a, b, c);

		long result = stopBenchmark();

		// Check result
		if (!a.multiply(b).isEqual(c)) {
			System.out.println("Matrix multiplication is not correct!!!");
			System.exit(1);
		}

		return result;
	}

	public void multiply(Matrix a, Matrix b, Matrix c) {
		if (a.getDim() <= Main.stopRecursionAt) {
			// c.set(0, 0, a.get(0, 0) * b.get(0, 0));
			c.multiply(a, b);
		} else {
			Matrix lhs = new Matrix(a.getDim());
			Matrix rhs = new Matrix(a.getDim());
			Matrix[][] aa = a.split(), bb = b.split();
			Matrix[][] ll = lhs.split(), rr = rhs.split();

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					multiply(aa[row][0], bb[0][col], ll[row][col]);
					multiply(aa[row][1], bb[1][col], rr[row][col]);
				}
			}

			// Do sum
			add(lhs, rhs, c);
		}
	}

	public void add(Matrix a, Matrix b, Matrix c) {
		if (a.getDim() <= Main.stopRecursionAt) {
			// c.set(0, 0, a.get(0, 0) + b.get(0, 0));
			c.add(a, b);
		} else {
			Matrix[][] aa = a.split(), bb = b.split(), cc = c.split();

			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 2; col++) {
					add(aa[row][col], bb[row][col], cc[row][col]);
				}
			}
		}
	}
}
