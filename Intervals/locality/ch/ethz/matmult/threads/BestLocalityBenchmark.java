package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestLocalityWorkerFactory extends WorkerFactory {
	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new BestLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class BestLocalityMultiplicationWorker extends MultiplicationWorker {
	public BestLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// Diagonal:
			// * 0, 3 -> node 0
			// * 1, 2 -> node 1
			//
			// Row:
			// * 0, 1 -> node 0
			// * 2, 3 -> node 1
			//
			// Column
			// * 0, 2 -> node 0
			// * 1, 3 -> node 1
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.units.getNode(0));
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
}

class BestLocalityAdditionWorker extends AdditionWorker {
	public BestLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// Diagonal:
			// * 0, 3 -> node 0
			// * 1, 2 -> node 1
			//
			// Row:
			// * 0, 1 -> node 0
			// * 2, 3 -> node 1
			//
			// Column
			// * 0, 2 -> node 0
			// * 1, 3 -> node 1
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.units.getNode(0));
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
}

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super(new BestLocalityWorkerFactory());
	}
}
