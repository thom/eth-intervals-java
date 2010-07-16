package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class IgnorantLocalityWorkerFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityMultiplicationWorker(dep, null, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityAdditionWorker(dep, null, a, b, c, quadrant);
	}
}

class IgnorantLocalityMultiplicationWorker extends MultiplicationTask {
	public IgnorantLocalityMultiplicationWorker(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new IgnorantLocalityWorkerFactory(), a, b, c,
				quadrant);
	}
}

class IgnorantLocalityAdditionWorker extends AdditionTask {
	public IgnorantLocalityAdditionWorker(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new IgnorantLocalityWorkerFactory(), a, b, c,
				quadrant);
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super(new IgnorantLocalityWorkerFactory());
	}
}