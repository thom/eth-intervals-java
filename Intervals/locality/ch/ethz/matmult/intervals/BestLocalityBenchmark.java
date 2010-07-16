package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestLocalityWorkerFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new BestLocalityMultiplicationWorker(dep, place, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new BestLocalityAdditionWorker(dep, place, a, b, c, quadrant);
	}
}

class BestLocalityMultiplicationWorker extends MultiplicationTask {
	public BestLocalityMultiplicationWorker(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new BestLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

class BestLocalityAdditionWorker extends AdditionTask {
	public BestLocalityAdditionWorker(Dependency dep, Place place, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new BestLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super(new BestLocalityWorkerFactory());
	}
}
