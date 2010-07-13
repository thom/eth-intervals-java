package ch.ethz.matmult;

import java.util.Random;

public class Matrix {
	private static Random random = new Random();

	private int dim;
	private int[][] data;
	private int rowDisplace;
	private int colDisplace;

	/**
	 * Constructor
	 * 
	 * @param d
	 *            dimension of zero-filled matrix
	 */
	public Matrix(int d) {
		dim = d;
		rowDisplace = colDisplace = 0;
		data = new int[d][d];
	}

	/**
	 * Constructor
	 * 
	 * @param matrix
	 *            backing array for matrix
	 * @param rowDisplace
	 *            offset of x origin
	 * @param colDisplace
	 *            offset of y origin
	 * @param dim
	 *            dimension
	 */
	public Matrix(int[][] matrix, int rowDisplace, int colDisplace, int dim) {
		data = matrix;
		this.rowDisplace = rowDisplace;
		this.colDisplace = colDisplace;
		this.dim = dim;
	}

	public static Matrix random(int n) {
		Matrix result = new Matrix(n);

		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				result.data[row + result.rowDisplace][col + result.colDisplace] = random
						.nextInt();
			}
		}
		return result;
	}

	public static Matrix random(int n, int upperBound) {
		Matrix result = new Matrix(n);

		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				result.data[row + result.rowDisplace][col + result.colDisplace] = random
						.nextInt(upperBound);
			}
		}
		return result;
	}

	/**
	 * Return value
	 * 
	 * @param row
	 *            coordinate
	 * @param col
	 *            coordinate
	 * @return value at coordinate
	 */
	public int get(int row, int col) {
		return data[row + rowDisplace][col + colDisplace];
	}

	/**
	 * Set value at coordinate
	 * 
	 * @param row
	 *            coordinate
	 * @param col
	 *            coordinate
	 * @param value
	 *            new value for position
	 */
	public void set(int row, int col, int value) {
		data[row + rowDisplace][col + colDisplace] = value;
	}

	/**
	 * Return matrix dimension
	 * 
	 * @return matrix dimension
	 **/
	public int getDim() {
		return dim;
	}

	/**
	 * Split matrix in array of half-size matrices
	 * 
	 * @return array of half-size matrices, backed by original
	 **/
	public Matrix[][] split() {
		Matrix[][] result = new Matrix[2][2];
		int newDim = dim / 2;
		result[0][0] = new Matrix(data, rowDisplace, colDisplace, newDim);
		result[0][1] = new Matrix(data, rowDisplace, colDisplace + newDim,
				newDim);
		result[1][0] = new Matrix(data, rowDisplace + newDim, colDisplace,
				newDim);
		result[1][1] = new Matrix(data, rowDisplace + newDim, colDisplace
				+ newDim, newDim);
		return result;
	}

	public Matrix add(Matrix b) {
		checkDim(b);
		Matrix c = new Matrix(dim);

		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				c.data[row][col] = data[row + rowDisplace][col + colDisplace]
						+ b.data[row + b.rowDisplace][col + b.colDisplace];
			}
		}

		return c;
	}

	public Matrix subtract(Matrix b) {
		checkDim(b);
		Matrix c = new Matrix(dim);

		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				c.data[row][col] = data[row + rowDisplace][col + colDisplace]
						- b.data[row + b.rowDisplace][col + b.colDisplace];
			}
		}

		return c;
	}

	public Matrix multiply(Matrix b) {
		checkDim(b);
		Matrix c = new Matrix(dim);

		for (int row = 0; row < dim; row++) {
			int[] ci = c.data[row];
			int[] ai = data[row + rowDisplace];
			for (int col2 = 0; col2 < dim; col2++) {
				int aik = ai[col2 + colDisplace];
				int[] bk = b.data[col2 + b.rowDisplace];
				for (int col1 = 0; col1 < dim; col1++) {
					ci[col1] += aik * bk[col1 + b.colDisplace];
				}
			}
		}

		return c;
	}

	public void add(Matrix a, Matrix b) {
		checkDim(a, b);
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				data[row + rowDisplace][col + colDisplace] = a.data[row
						+ a.rowDisplace][col + a.colDisplace]
						+ b.data[row + b.rowDisplace][col + b.colDisplace];
			}
		}
	}

	public void subtract(Matrix a, Matrix b) {
		checkDim(a, b);
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				data[row + rowDisplace][col + colDisplace] = a.data[row
						+ a.rowDisplace][col + a.colDisplace]
						- b.data[row + b.rowDisplace][col + b.colDisplace];
			}
		}
	}

	public void multiply(Matrix a, Matrix b) {
		checkDim(a, b);

		for (int row = 0; row < dim; row++) {
			int[] ci = data[row + rowDisplace];
			int[] ai = a.data[row + a.rowDisplace];
			for (int col2 = 0; col2 < dim; col2++) {
				int aik = ai[col2 + a.colDisplace];
				int[] bk = b.data[col2 + b.rowDisplace];
				for (int col1 = 0; col1 < dim; col1++) {
					if (col2 == 0)
						ci[col1 + colDisplace] = 0;
					ci[col1 + colDisplace] += aik * bk[col1 + b.colDisplace];
				}
			}
		}
	}

	public boolean isEqual(Matrix b) {
		checkDim(b);

		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				if ((data[row + colDisplace][col + rowDisplace] - b.data[row
						+ b.colDisplace][col + b.rowDisplace]) != 0) {
					return false;
				}
			}
		}

		return true;
	}

	public String toString() {
		String result = "";
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				result += data[row + colDisplace][col + rowDisplace] + "\t";
			}
			if (row < dim - 1) {
				result += "\n";
			}
		}
		return result;
	}

	private void checkDim(Matrix b) {
		if (b.dim != dim) {
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

	private void checkDim(Matrix a, Matrix b) {
		if (!((a.dim == dim) && (b.dim == dim))) {
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}
}
