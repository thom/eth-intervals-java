package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestDiagLocalityMultiplicationWorker extends MultiplicationWorker {
	public BestDiagLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant2:
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
		return new BestDiagLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestDiagLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class BestDiagLocalityAdditionWorker extends AdditionWorker {
	public BestDiagLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant2:
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
		return new BestDiagLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestDiagLocalityAdditionWorker(a, b, c, quadrant);
	}
}

public class BestDiagLocalityBenchmark extends Benchmark {
	public BestDiagLocalityBenchmark() {
		super();
	}

	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new BestDiagLocalityMultiplicationWorker(a, b, c, quadrant);
	}
}
