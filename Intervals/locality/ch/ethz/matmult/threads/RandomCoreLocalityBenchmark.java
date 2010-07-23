package ch.ethz.matmult.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomCoreLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomCoreLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomCoreLocalityAdditionTask(a, b, c, quadrant);
	}
}

class RandomCoreLocalityMultiplicationTask extends MultiplicationTask {
	private Random random;

	public RandomCoreLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomCoreLocalityTaskFactory(), a, b, c, quadrant);
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
}

class RandomCoreLocalityAdditionTask extends AdditionTask {
	private Random random;

	public RandomCoreLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomCoreLocalityTaskFactory(), a, b, c, quadrant);
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
}

public class RandomCoreLocalityBenchmark extends Benchmark {
	public RandomCoreLocalityBenchmark() {
		super(new RandomCoreLocalityTaskFactory());
	}
}
