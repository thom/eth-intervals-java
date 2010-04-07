package ch.ethz.intervals;

import java.util.Random;

public class MatrixMultiplication {
	private int size;
	private double[][] a;
	private double[][] b;
	private double[][] c;

	public MatrixMultiplication(int size) {
		this.size = size;
		a = createRandDoubleArray(size);
		b = createRandDoubleArray(size);
		c = new double[size][size];
	}

	public void multiply() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					c[i][j] += a[i][k] * b[k][j];
				}
			}
		}
	}

	private double[][] createRandDoubleArray(int dimension) {
		double[][] result = new double[dimension][dimension];
		Random random = new Random();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				result[i][j] = random.nextDouble();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		MatrixMultiplication mm = new MatrixMultiplication(Integer
				.parseInt(args[0]));
		mm.multiply();
	}
}
