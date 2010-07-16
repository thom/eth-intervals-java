package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestColLocalityWorkerFactory extends WorkerFactory {
	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new BestColLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestColLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class BestColLocalityMultiplicationWorker extends MultiplicationWorker {
	public BestColLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestColLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant2:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant3:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class BestColLocalityAdditionWorker extends AdditionWorker {
	public BestColLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestColLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// TODO: keep only one best case locality
			// TODO: fix assignment of quadrants
			switch (quadrant) {
			case Quadrant0:
			case Quadrant2:
				Affinity.set(Main.units.getNode(0 % Main.units.nodesSize()));
				break;
			case Quadrant1:
			case Quadrant3:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

public class BestColLocalityBenchmark extends Benchmark {
	public BestColLocalityBenchmark() {
		super(new BestColLocalityWorkerFactory());
	}
}
