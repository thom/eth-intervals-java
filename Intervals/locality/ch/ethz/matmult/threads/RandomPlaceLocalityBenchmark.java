package ch.ethz.matmult.threads;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomPlaceLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		return new RandomPlaceLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomPlaceLocalityAdditionTask(a, b, c, quadrant);
	}
}

class RandomPlaceLocalityMultiplicationTask extends MultiplicationTask {
	private Random random;

	public RandomPlaceLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomPlaceLocalityTaskFactory(), a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.places.get(random.nextInt(Main.places.length)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class RandomPlaceLocalityAdditionTask extends AdditionTask {
	private Random random;

	public RandomPlaceLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new RandomPlaceLocalityTaskFactory(), a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Main.places.get(random.nextInt(Main.places.length)));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class RandomPlaceLocalityBenchmark extends Benchmark {
	public RandomPlaceLocalityBenchmark() {
		super(new RandomPlaceLocalityTaskFactory());
	}
}
