package ch.ethz.matmult.threads;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;
import ch.ethz.util.LocalityBenchmark;
import ch.ethz.util.StopWatch;

public abstract class Benchmark extends LocalityBenchmark {
	protected abstract MultiplicationWorker createMultiplicationWorker(
			Matrix a, Matrix b, Matrix c, Quadrant quadrant);

	public long run() {
		StopWatch stopWatch = new StopWatch();

		// Create matrices
		Matrix a = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix b = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix c = new Matrix(Main.matrixDimension);

		// Clean JVM
		cleanJvm();

		// Start stop watch
		stopWatch.start();

		// Create worker
		MultiplicationWorker multiplicationWorker = createMultiplicationWorker(
				a, b, c, Quadrant.None);

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
		}

		// Clean JVM
		cleanJvm();

		return stopWatch.getElapsedTime();
	}
}
