package ch.ethz.matmult.threads;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;
import ch.ethz.util.LocalityBenchmark;

public abstract class Benchmark extends LocalityBenchmark {
	protected TaskFactory factory;

	public Benchmark(TaskFactory factory) {
		this.factory = factory;
	}

	public long run() {
		startBenchmark();

		// Create matrices
		Matrix a = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix b = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix c = new Matrix(Main.matrixDimension);

		// Create task
		MultiplicationTask multiplicationTask = factory
				.createMultiplicationTask(a, b, c, Quadrant.None);

		// Start task
		multiplicationTask.start();

		// Wait for task to finish
		try {
			multiplicationTask.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long result = stopBenchmark();

		// Check result
		if (!a.multiply(b).isEqual(c)) {
			System.out.println("Matrix multiplication is not correct!!!");
			System.exit(1);
		}

		return result;
	}
}
