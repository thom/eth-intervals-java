package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestRowLocalityMultiplicationWorker extends MultiplicationWorker {
	public BestRowLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant1:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant2:
			case Quadrant3:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
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
		return new BestRowLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestRowLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class BestRowLocalityAdditionWorker extends AdditionWorker {
	public BestRowLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant1:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant2:
			case Quadrant3:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
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
		return new BestRowLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestRowLocalityAdditionWorker(a, b, c, quadrant);
	}
}

public class BestRowLocalityBenchmark extends Benchmark {
	public BestRowLocalityBenchmark() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new BestRowLocalityMultiplicationWorker(a, b, c, quadrant);
	}
}
