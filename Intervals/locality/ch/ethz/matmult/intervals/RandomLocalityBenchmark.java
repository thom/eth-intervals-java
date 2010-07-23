package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityMultiplicationTask(dep, place, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		// TODO: Set place
		return new RandomLocalityAdditionTask(dep, place, a, b, c, quadrant);
	}
}

class RandomLocalityMultiplicationTask extends MultiplicationTask {
	public RandomLocalityMultiplicationTask(Dependency dep, Place place,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class RandomLocalityAdditionTask extends AdditionTask {
	public RandomLocalityAdditionTask(Dependency dep, Place place, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, place, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super(new RandomLocalityTaskFactory());
	}
}
