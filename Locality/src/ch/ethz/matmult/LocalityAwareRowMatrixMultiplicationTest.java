package ch.ethz.matmult;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareRowMatrixMultiplicationWorker extends MultiplicationWorker {
	public LocalityAwareRowMatrixMultiplicationWorker(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
			case Quadrant1:
				Affinity.set(Config.units.getNode(0 % Config.units.nodesSize()));
				break;
			case Quadrant2:
			case Quadrant3:
				Affinity.set(Config.units.getNode(1 % Config.units.nodesSize()));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityAwareRowMatrixMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityAwareRowMatrixAdditionWorker(a, b, c, quadrant);
	}
}

class LocalityAwareRowMatrixAdditionWorker extends AdditionWorker {
	public LocalityAwareRowMatrixAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
			case Quadrant1:
				Affinity.set(Config.units.getNode(0 % Config.units.nodesSize()));
				break;
			case Quadrant2:
			case Quadrant3:
				Affinity.set(Config.units.getNode(1 % Config.units.nodesSize()));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityAwareRowMatrixMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityAwareRowMatrixAdditionWorker(a, b, c, quadrant);
	}
}

public class LocalityAwareRowMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public LocalityAwareRowMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityAwareRowMatrixMultiplicationWorker(a, b, c, quadrant);
	}
}
