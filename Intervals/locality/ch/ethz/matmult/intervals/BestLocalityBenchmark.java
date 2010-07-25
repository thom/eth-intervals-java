package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			PlaceID placeID, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new BestLocalityMultiplicationTask(dep, placeID, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new BestLocalityAdditionTask(dep, placeID, a, b, c, quadrant);
	}
}

class BestLocalityMultiplicationTask extends MultiplicationTask {
	public BestLocalityMultiplicationTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new BestLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class BestLocalityAdditionTask extends AdditionTask {
	public BestLocalityAdditionTask(Dependency dep, PlaceID placeID, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new BestLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class BestLocalityBenchmark extends Benchmark {
	public BestLocalityBenchmark() {
		super(new BestLocalityTaskFactory());
	}
}
