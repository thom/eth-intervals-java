package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class IgnorantLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityMultiplicationTask(dep, null, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityAdditionTask(dep, null, a, b, c, quadrant);
	}
}

class IgnorantLocalityMultiplicationTask extends MultiplicationTask {
	public IgnorantLocalityMultiplicationTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new IgnorantLocalityTaskFactory(), a, b, c,
				quadrant);
	}
}

class IgnorantLocalityAdditionTask extends AdditionTask {
	public IgnorantLocalityAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new IgnorantLocalityTaskFactory(), a, b, c,
				quadrant);
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super(new IgnorantLocalityTaskFactory());
	}
}