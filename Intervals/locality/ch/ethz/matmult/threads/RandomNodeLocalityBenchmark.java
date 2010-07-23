package ch.ethz.matmult.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomNodeLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomNodeLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomNodeLocalityAdditionTask(a, b, c, quadrant);
	}
}

class RandomNodeLocalityMultiplicationTask extends MultiplicationTask {
	private Random random;

	public RandomNodeLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomNodeLocalityTaskFactory(), a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(random.nextInt(Main.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomNodeLocalityAdditionTask extends AdditionTask {
	private Random random;

	public RandomNodeLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomNodeLocalityTaskFactory(), a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.units.getNode(random.nextInt(Main.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomNodeLocalityBenchmark extends Benchmark {
	public RandomNodeLocalityBenchmark() {
		super(new RandomNodeLocalityTaskFactory());
	}
}
