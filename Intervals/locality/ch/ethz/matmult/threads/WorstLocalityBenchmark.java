package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class WorstLocalityWorkerFactory extends WorkerFactory {
	@Override
	protected MultiplicationWorker createMultiplicationWorker(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new WorstLocalityMultiplicationWorker(a, b, c, quadrant);
	}

	@Override
	protected AdditionWorker createAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new WorstLocalityAdditionWorker(a, b, c, quadrant);
	}
}

class WorstLocalityMultiplicationWorker extends MultiplicationWorker {
	public WorstLocalityMultiplicationWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new WorstLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
				break;
			case Quadrant1:
				Affinity.set(Main.units.getNode(0));
				break;
			case Quadrant2:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
				break;
			case Quadrant3:
				Affinity.set(Main.units.getNode(0));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class WorstLocalityAdditionWorker extends AdditionWorker {
	public WorstLocalityAdditionWorker(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new WorstLocalityWorkerFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			switch (quadrant) {
			case Quadrant0:
				Affinity.set(Main.units.getNode(0));
				break;
			case Quadrant1:
				Affinity.set(Main.units.getNode(1 % Main.units.nodesSize()));
				break;
			case Quadrant2:
				Affinity.set(Main.units.getNode(0));
				break;
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

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super(new WorstLocalityWorkerFactory());
	}
}
