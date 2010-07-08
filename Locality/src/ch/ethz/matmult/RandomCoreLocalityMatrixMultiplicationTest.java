package ch.ethz.matmult;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomCoreLocalityMultiplicationWorker extends MultiplicationWorker {
	private Random random;

	public RandomCoreLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.get(random.nextInt(Config.units.size())));
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
			Affinity.set(Config.units.get(random.nextInt(Config.units.size())));
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

public class RandomCoreLocalityMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public RandomCoreLocalityMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomCoreLocalityMultiplicationWorker(a, b, c, quadrant);
	}
}
