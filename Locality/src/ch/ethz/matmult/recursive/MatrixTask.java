package ch.ethz.matmult.recursive;

import ch.ethz.matmult.Matrix;

/**
 * Parallel Matrix Multiplication
 */
public class MatrixTask {
	static Matrix add(Matrix a, Matrix b) throws InterruptedException {
		int n = a.getDim();
		Matrix c = new Matrix(n);
		AddTask addTask = new AddTask(a, b, c);
		addTask.start();
		addTask.join();
		return c;
	}

	static Matrix multiply(Matrix a, Matrix b) throws InterruptedException {
		int n = a.getDim();
		Matrix c = new Matrix(n);
		MulTask mulTask = new MulTask(a, b, c);
		mulTask.start();
		mulTask.join();
		return c;
	}

	static class MulTask extends Thread {
		Matrix a, b, c, lhs, rhs;

		public MulTask(Matrix a, Matrix b, Matrix c) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.lhs = new Matrix(a.getDim());
			this.rhs = new Matrix(a.getDim());
		}

		public void run() {
			if (a.getDim() <= 4) {
				// c.set(0, 0, a.get(0, 0) * b.get(0, 0));
				c.multiply(a, b);
			} else {
				Matrix[][] aa = a.split(), bb = b.split();
				Matrix[][] ll = lhs.split(), rr = rhs.split();

				MulTask[][][] mulTasks = new MulTask[2][2][2];

				for (int row = 0; row < 2; row++) {
					for (int col = 0; col < 2; col++) {
						mulTasks[row][col][0] = new MulTask(aa[row][0],
								bb[0][col], ll[row][col]);
						mulTasks[row][col][1] = new MulTask(aa[row][1],
								bb[1][col], rr[row][col]);
						mulTasks[row][col][0].start();
						mulTasks[row][col][1].start();
					}
				}

				for (int row = 0; row < 2; row++) {
					for (int col = 0; col < 2; col++) {
						try {
							mulTasks[row][col][0].join();
							mulTasks[row][col][1].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				// Do sum
				Thread addTask = new AddTask(lhs, rhs, c);
				addTask.start();
				try {
					addTask.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class AddTask extends Thread {
		Matrix a, b, c;

		public AddTask(Matrix a, Matrix b, Matrix c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public void run() {
			if (a.getDim() <= 4) {
				// c.set(0, 0, a.get(0, 0) + b.get(0, 0));
				c.add(a, b);
			} else {
				Matrix[][] aa = a.split(), bb = b.split(), cc = c.split();

				AddTask[][] addTasks = new AddTask[2][2];

				for (int row = 0; row < 2; row++) {
					for (int col = 0; col < 2; col++) {
						addTasks[row][col] = new AddTask(aa[row][col],
								bb[row][col], cc[row][col]);
						addTasks[row][col].start();
					}
				}

				for (int row = 0; row < 2; row++) {
					for (int col = 0; col < 2; col++) {
						try {
							addTasks[row][col].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}