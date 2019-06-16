package ch.ethz.matmult.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;
import ch.ethz.util.LocalityBenchmark;

public class Benchmark extends LocalityBenchmark {
	protected static ExecutorService exec;
	protected TaskFactory factory;

	public Benchmark() {
		this.factory = new TaskFactory();
	}

	public long run() {
		startBenchmark();
		// exec = Executors.newFixedThreadPool(Main.threads);
		exec = Executors.newCachedThreadPool();

		// Create matrices
		Matrix a = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix b = Matrix.random(Main.matrixDimension, Main.upperBound);
		Matrix c = new Matrix(Main.matrixDimension);

		// Do multiplication
		try {
			exec.submit(
					factory.createMultiplicationTask(a, b, c, Quadrant.None))
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		exec.shutdown();
		long result = stopBenchmark();

		// Check result
		if (!a.multiply(b).isEqual(c)) {
			System.out.println("Matrix multiplication is not correct!!!");
			System.exit(1);
		}

		return result;
	}
}
