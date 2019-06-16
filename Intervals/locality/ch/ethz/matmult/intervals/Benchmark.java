package ch.ethz.matmult.intervals;

import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;
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
		final Matrix a = Matrix.random(Main.matrixDimension, Main.upperBound);
		final Matrix b = Matrix.random(Main.matrixDimension, Main.upperBound);
		final Matrix c = new Matrix(Main.matrixDimension);

		// Do multiplication
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				factory.createMultiplicationTask(subinterval, a, b, c,
						Quadrant.None);
			}
		});

		long result = stopBenchmark();

		// Check result
		/*
		if (!a.multiply(b).isEqual(c)) {
			System.out.println("Matrix multiplication is not correct!!!");
			System.exit(1);
		}
		*/

		return result;
	}
}
