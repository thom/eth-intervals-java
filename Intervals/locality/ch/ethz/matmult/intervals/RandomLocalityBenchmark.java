package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomLocalityWorkerFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityMultiplicationWorker(dep, place, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityAdditionWorker(dep, place, a, b, c, quadrant);
	}
}

class RandomLocalityMultiplicationWorker extends MultiplicationTask {
	public RandomLocalityMultiplicationWorker(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new RandomLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

class RandomLocalityAdditionWorker extends AdditionTask {
	public RandomLocalityAdditionWorker(Dependency dep, Place place, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new RandomLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super(new RandomLocalityWorkerFactory());
	}
}
