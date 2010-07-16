package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class WorstLocalityWorkerFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityMultiplicationWorker(dep, place, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityAdditionWorker(dep, place, a, b, c, quadrant);
	}
}

class WorstLocalityMultiplicationWorker extends MultiplicationTask {
	public WorstLocalityMultiplicationWorker(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new WorstLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

class WorstLocalityAdditionWorker extends AdditionTask {
	public WorstLocalityAdditionWorker(Dependency dep, Place place, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new WorstLocalityWorkerFactory(), a, b, c, quadrant);
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super(new WorstLocalityWorkerFactory());
	}
}
