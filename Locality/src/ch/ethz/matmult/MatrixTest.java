package ch.ethz.matmult;

import java.util.concurrent.ExecutionException;


public class MatrixTest {
	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		Matrix a = Matrix.random(8, 5);
		Matrix b = Matrix.random(8, 5);
		Matrix c = new Matrix(8);

		System.out.println(b + "\n");
		System.out.println(a + "\n");

		// Test matrix addition
		System.out.println(a.add(b) + "\n");
		c.add(a, b);
		System.out.println(c + "\n");
		System.out.println(MatrixTask.add(a, b) + "\n");
		System.out.println(a.add(b).isEqual(c) + "\n");
		System.out.println(a.add(b).isEqual(MatrixTask.add(a, b)) + "\n");

		// Test matrix multiplication
		System.out.println(a.multiply(b) + "\n");
		c.multiply(a, b);
		System.out.println(c + "\n");
		System.out.println(MatrixTask.multiply(a, b) + "\n");
		System.out.println(a.multiply(b).isEqual(c) + "\n");
		System.out.println(a.multiply(b).isEqual(MatrixTask.multiply(a, b))
				+ "\n");
	}
}
