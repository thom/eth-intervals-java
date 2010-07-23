package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class IgnorantLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityMultiplicationTask(dep, null, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new IgnorantLocalityAdditionTask(dep, null, a, b, c, quadrant);
	}
}

class IgnorantLocalityMultiplicationTask extends MultiplicationTask {
	public IgnorantLocalityMultiplicationTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new IgnorantLocalityTaskFactory(), a, b, c,
				quadrant);
	}
}

class IgnorantLocalityAdditionTask extends AdditionTask {
	public IgnorantLocalityAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new IgnorantLocalityTaskFactory(), a, b, c,
				quadrant);
	}
}

public class IgnorantLocalityBenchmark extends Benchmark {
	public IgnorantLocalityBenchmark() {
		super(new IgnorantLocalityTaskFactory());
	}
}