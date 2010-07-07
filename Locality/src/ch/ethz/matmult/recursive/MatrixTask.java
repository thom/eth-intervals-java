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

				// TODO: use loop to initialize and start threads
				MulTask mulTaskLeft00 = new MulTask(aa[0][0], bb[0][0],
						ll[0][0]);
				MulTask mulTaskLeft01 = new MulTask(aa[0][0], bb[0][1],
						ll[0][1]);
				MulTask mulTaskLeft10 = new MulTask(aa[1][0], bb[0][0],
						ll[1][0]);
				MulTask mulTaskLeft11 = new MulTask(aa[1][0], bb[0][1],
						ll[1][1]);
				MulTask mulTaskRight00 = new MulTask(aa[0][1], bb[1][0],
						rr[0][0]);
				MulTask mulTaskRight01 = new MulTask(aa[0][1], bb[1][1],
						rr[0][1]);
				MulTask mulTaskRight10 = new MulTask(aa[1][1], bb[1][0],
						rr[1][0]);
				MulTask mulTaskRight11 = new MulTask(aa[1][1], bb[1][1],
						rr[1][1]);

				mulTaskLeft00.start();
				mulTaskLeft01.start();
				mulTaskLeft10.start();
				mulTaskLeft11.start();
				mulTaskRight00.start();
				mulTaskRight01.start();
				mulTaskRight10.start();
				mulTaskRight11.start();

				try {
					mulTaskLeft00.join();
					mulTaskLeft01.join();
					mulTaskLeft10.join();
					mulTaskLeft11.join();
					mulTaskRight00.join();
					mulTaskRight01.join();
					mulTaskRight10.join();
					mulTaskRight11.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
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

				// TODO: use loop to initialize and start threads
				AddTask addTask00 = new AddTask(aa[0][0], bb[0][0], cc[0][0]);
				AddTask addTask01 = new AddTask(aa[0][1], bb[0][1], cc[0][1]);
				AddTask addTask10 = new AddTask(aa[1][0], bb[1][0], cc[1][0]);
				AddTask addTask11 = new AddTask(aa[1][1], bb[1][1], cc[1][1]);

				addTask00.start();
				addTask01.start();
				addTask10.start();
				addTask11.start();

				try {
					addTask00.join();
					addTask01.join();
					addTask10.join();
					addTask11.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}