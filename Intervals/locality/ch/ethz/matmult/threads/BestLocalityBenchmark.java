package ch.ethz.matmult.threads;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Matrix a, Matrix b,
			Matrix c, Quadrant quadrant) {
		return new BestLocalityMultiplicationTask(a, b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		return new BestLocalityAdditionTask(a, b, c, quadrant);
	}
}

class BestLocalityMultiplicationTask extends MultiplicationTask {
	public BestLocalityMultiplicationTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestLocalityTaskFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// Diagonal:
			// * 0, 3 -> place 0
			// * 1, 2 -> place 1
			//
			// Row:
			// * 0, 1 -> place 0
			// * 2, 3 -> place 1
			//
			// Column
			// * 0, 2 -> place 0
			// * 1, 3 -> place 1
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.places.get(0));
				break;
			case Quadrant1:
			case Quadrant2:
				Affinity.set(Main.places.get(1));
				break;
			}
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

		super.run();
	}
}

class BestLocalityAdditionTask extends AdditionTask {
	public BestLocalityAdditionTask(Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(new BestLocalityTaskFactory(), a, b, c, quadrant);
	}

	public void run() {
		try {
			// Diagonal:
			// * 0, 3 -> place 0
			// * 1, 2 -> place 1
			//
			// Row:
			// * 0, 1 -> place 0
			// * 2, 3 -> place 1
			//
			// Column
			// * 0, 2 -> place 0
			// * 1, 3 -> place 1
			switch (quadrant) {
			case Quadrant0:
			case Quadrant3:
				Affinity.set(Main.places.get(0));
				break;
			case Quadrant1:
			case Quadrant2:
				Affinity.set(Main.places.get(1));
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
		super(new BestLocalityTaskFactory());
	}
}
