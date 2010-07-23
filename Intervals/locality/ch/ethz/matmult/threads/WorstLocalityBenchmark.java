package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class WorstLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new WorstLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new WorstLocalityAdditionTask(a, b, c, quadrant);
	}
}

class WorstLocalityMultiplicationTask extends MultiplicationTask {
	public WorstLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new WorstLocalityTaskFactory(), a, b, c, quadrant);
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

class WorstLocalityAdditionTask extends AdditionTask {
	public WorstLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new WorstLocalityTaskFactory(), a, b, c, quadrant);
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
		super(new WorstLocalityTaskFactory());
	}
}
