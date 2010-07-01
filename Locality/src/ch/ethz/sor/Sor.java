package ch.ethz.sor;

import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Sor {
	public final static int N = 500;
	public final static int M = 500;

	public static int iterations = 100;
	public static float[][] black = new float[M + 2][N + 1];
	public static float[][] red = new float[M + 2][N + 1];
	public static int numberOfThreads = 1;
	public static CyclicBarrier barrier;
	public static Thread[] threads;

	public static void main(String[] args) {
		try {
			numberOfThreads = Integer.parseInt(args[1]);
			iterations = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out
					.println("usage: java Sor <iterations> <number of threads>");
			System.exit(-1);
		}

		threads = new Thread[numberOfThreads];
		barrier = new CyclicBarrier(numberOfThreads);

		// initialize arrays
		int firstRow = 1;
		int lastRow = M;

		for (int i = firstRow; i <= lastRow; i++) {
			// Initialize the top edge
			if (i == 1)
				for (int j = 0; j <= N; j++)
					red[0][j] = black[0][j] = (float) 1.0;

			// Initialize the left and right edges
			if ((i & 1) != 0) {
				red[i][0] = (float) 1.0;
				black[i][N] = (float) 1.0;
			} else {
				black[i][0] = (float) 1.0;
				red[i][N] = (float) 1.0;
			}

			// Initialize the bottom edge.
			if (i == M)
				for (int j = 0; j <= N; j++)
					red[i + 1][j] = black[i + 1][j] = (float) 1.0;
		}

		// Start computation
		System.gc();
		long start = new Date().getTime();

		for (int id = 0; id < numberOfThreads; id++) {
			firstRow = (M * id) / numberOfThreads + 1;
			lastRow = (M * (id + 1)) / numberOfThreads;

			if ((firstRow & 1) != 0)
				threads[id] = new SorFirstRowOdd(firstRow, lastRow);
			else
				threads[id] = new SorFirstRowEven(firstRow, lastRow);
			threads[id].start();
		}

		for (int id = 0; id < numberOfThreads; id++) {
			try {
				threads[id].join();
			} catch (InterruptedException e) {
			}
		}

		long end = new Date().getTime();

		System.out.println("Sor-" + numberOfThreads + "\t"
				+ Long.toString(end - start));

		// Print results
		float redSum = 0, blackSum = 0;
		for (int i = 0; i < M + 2; i++)
			for (int j = 0; j < N + 1; j++) {
				redSum += red[i][j];
				blackSum += black[i][j];
			}
		System.out.println("Exiting. red sum = " + redSum + ", black sum = "
				+ blackSum);
	}

	public static void print(String s) {
		System.out.println(Thread.currentThread().getName() + ":" + s);
	}
}

class SorFirstRowOdd extends Thread {
	int firstRow, end;
	int N = Sor.N;
	int M = Sor.M;
	float[][] black = Sor.black;
	float[][] red = Sor.red;

	public SorFirstRowOdd(int firstRow, int end) {
		this.firstRow = firstRow;
		this.end = end;
	}

	public void run() {
		int i, j, k;

		for (i = 0; i < Sor.iterations; i++) {
			// Sor.print("iteration A " + i);
			for (j = firstRow; j <= end; j++) {
				for (k = 0; k < N; k++) {
					black[j][k] = (red[j - 1][k] + red[j + 1][k] + red[j][k] + red[j][k + 1])
							/ (float) 4.0;
				}

				if ((j += 1) > end)
					break;

				for (k = 1; k <= N; k++) {
					black[j][k] = (red[j - 1][k] + red[j + 1][k]
							+ red[j][k - 1] + red[j][k])
							/ (float) 4.0;
				}
			}

			try {
				// Sor.print("barrier 1a - " + System.currentTimeMillis());
				Sor.barrier.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (BrokenBarrierException e) {
				throw new RuntimeException(e);
			}

			for (j = firstRow; j <= end; j++) {
				for (k = 1; k <= N; k++) {
					red[j][k] = (black[j - 1][k] + black[j + 1][k]
							+ black[j][k - 1] + black[j][k])
							/ (float) 4.0;
				}

				if ((j += 1) > end)
					break;

				for (k = 0; k < N; k++) {
					red[j][k] = (black[j - 1][k] + black[j + 1][k]
							+ black[j][k] + black[j][k + 1])
							/ (float) 4.0;
				}
			}

			try {
				// Sor.print("barrier 2a - " + System.currentTimeMillis());
				Sor.barrier.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

class SorFirstRowEven extends Thread {
	int firstRow, end;
	int N = Sor.N;
	int M = Sor.M;
	float[][] black = Sor.black;
	float[][] red = Sor.red;

	public SorFirstRowEven(int firstRow, int end) {
		this.firstRow = firstRow;
		this.end = end;
	}

	public void run() {
		int i, j, k;

		for (i = 0; i < Sor.iterations; i++) {
			// Sor.print("iteration B " + i);
			for (j = firstRow; j <= end; j++) {

				for (k = 1; k <= N; k++) {

					black[j][k] = (red[j - 1][k] + red[j + 1][k]
							+ red[j][k - 1] + red[j][k])
							/ (float) 4.0;
				}
				if ((j += 1) > end)
					break;

				for (k = 0; k < N; k++) {

					black[j][k] = (red[j - 1][k] + red[j + 1][k] + red[j][k] + red[j][k + 1])
							/ (float) 4.0;
				}
			}

			try {
				// Sor.print("barrier 1b - " + System.currentTimeMillis());
				Sor.barrier.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (BrokenBarrierException e) {
				throw new RuntimeException(e);
			}

			for (j = firstRow; j <= end; j++) {

				for (k = 0; k < N; k++) {

					red[j][k] = (black[j - 1][k] + black[j + 1][k]
							+ black[j][k] + black[j][k + 1])
							/ (float) 4.0;
				}
				if ((j += 1) > end)
					break;

				for (k = 1; k <= N; k++) {

					red[j][k] = (black[j - 1][k] + black[j + 1][k]
							+ black[j][k - 1] + black[j][k])
							/ (float) 4.0;
				}
			}

			try {
				// Sor.print("barrier 2b - " + System.currentTimeMillis());
				Sor.barrier.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
		}
	}
}