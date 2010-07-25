package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class WorstLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			PlaceID placeID, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityMultiplicationTask(dep, placeID, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new WorstLocalityAdditionTask(dep, placeID, a, b, c, quadrant);
	}
}

class WorstLocalityMultiplicationTask extends MultiplicationTask {
	public WorstLocalityMultiplicationTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new WorstLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class WorstLocalityAdditionTask extends AdditionTask {
	public WorstLocalityAdditionTask(Dependency dep, PlaceID placeID, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new WorstLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class WorstLocalityBenchmark extends Benchmark {
	public WorstLocalityBenchmark() {
		super(new WorstLocalityTaskFactory());
	}
}
