package ch.ethz.matmult;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class LocalityAwareDiagMatrixMultiplicationWorker extends MultiplicationWorker {
	public LocalityAwareDiagMatrixMultiplicationWorker(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Config.units.getNode(0 % Config.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant2:
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
		return new LocalityAwareDiagMatrixMultiplicationWorker(a, b, c,
				quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityAwareDiagMatrixAdditionWorker(a, b, c, quadrant);
	}
}

class LocalityAwareDiagMatrixAdditionWorker extends AdditionWorker {
	public LocalityAwareDiagMatrixAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Config.units.getNode(0 % Config.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant2:
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
		return new LocalityAwareDiagMatrixMultiplicationWorker(a, b, c,
				quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new LocalityAwareDiagMatrixAdditionWorker(a, b, c, quadrant);
	}
}

public class LocalityAwareDiagMatrixMultiplicationTest extends
		MatrixMultiplicationTest {
	public LocalityAwareDiagMatrixMultiplicationTest() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new LocalityAwareDiagMatrixMultiplicationWorker(a, b, c,
				quadrant);
	}
}
