package ch.ethz.matmult;

import ch.ethz.util.StopWatch;

public abstract class MatrixMultiplicationTest {
	protected abstract MultiplicationWorker createMultiplicationWorker(int id,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Create matrices
		Matrix a = Matrix.random(Config.MATRIX_DIMENSION, Config.UPPER_BOUND);
		Matrix b = Matrix.random(Config.MATRIX_DIMENSION, Config.UPPER_BOUND);
		Matrix c = new Matrix(Config.MATRIX_DIMENSION);

		// Clean JVM
		cleanJvm();

		// Start stop watch
		stopWatch.start();

		// Create worker
		MultiplicationWorker multiplicationWorker = createMultiplicationWorker(
				0, a, b, c, Quadrant.None);

		// Start worker
		multiplicationWorker.start();

		// Wait for worker to finish
		try {
			multiplicationWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop stop watch
		stopWatch.stop();

		// Check result
		if (!a.multiply(b).isEqual(c)) {
			System.out.println("Matrix multiplication is not correct!!!");
			System.exit(1);
		} else {
			System.out.println("FUCKKKKK");
		}

		// Clean JVM
		cleanJvm();

		return stopWatch.getElapsedTime();
	}

	private void cleanJvm() {
		System.runFinalization();
		System.gc();
	}
}
