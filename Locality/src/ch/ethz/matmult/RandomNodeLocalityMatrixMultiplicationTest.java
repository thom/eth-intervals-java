package ch.ethz.matmult;

import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class RandomNodeLocalityMultiplicationWorker extends MultiplicationWorker {
	private Random random;

	public RandomNodeLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.getNode(random.nextInt(Config.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomNodeLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomNodeLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class RandomNodeLocalityAdditionWorker extends AdditionWorker {
	private Random random;

	public RandomNodeLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
		random = new Random();
	}

	public void run() {
		try {
			Affinity.set(Config.units.getNode(random.nextInt(Config.units
					.nodesSize())));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomNodeLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new RandomNodeLocalityAdditionWorker(a, b, c, quadrant);
	}
}

public class RandomNodeLocalityMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public RandomNodeLocalityMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomNodeLocalityMultiplicationWorker(a, b, c, quadrant);
	}
}
