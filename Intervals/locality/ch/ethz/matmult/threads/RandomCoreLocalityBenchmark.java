package ch.ethz.matmult.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomCoreLocalityMultiplicationWorker extends MultiplicationWorker {
	private Random random;

	public RandomCoreLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(Main.units.size())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomCoreLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomCoreLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class RandomCoreLocalityAdditionWorker extends AdditionWorker {
	private Random random;

	public RandomCoreLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.get(random.nextInt(Main.units.size())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomCoreLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomCoreLocalityAdditionWorker(a, b, c, quadrant);
	}
}

public class RandomCoreLocalityBenchmark extends
		Benchmark {
	public RandomCoreLocalityBenchmark() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomCoreLocalityMultiplicationWorker(a, b, c, quadrant);
	}
}
