package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class WorstLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityMultiplicationTask(dep, place, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityAdditionTask(dep, place, a, b, c, quadrant);
	}
}

class WorstLocalityMultiplicationTask extends MultiplicationTask {
	public WorstLocalityMultiplicationTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new WorstLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class WorstLocalityAdditionTask extends AdditionTask {
	public WorstLocalityAdditionTask(Dependency dep, Place place, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new WorstLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super(new WorstLocalityTaskFactory());
	}
}
