package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			PlaceID placeID, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityMultiplicationTask(dep, placeID, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityAdditionTask(dep, placeID, a, b, c, quadrant);
	}
}

class RandomLocalityMultiplicationTask extends MultiplicationTask {
	public RandomLocalityMultiplicationTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class RandomLocalityAdditionTask extends AdditionTask {
	public RandomLocalityAdditionTask(Dependency dep, PlaceID placeID, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super(new RandomLocalityTaskFactory());
	}
}
