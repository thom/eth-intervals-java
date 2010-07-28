package ch.ethz.matmult.intervals;

import java.util.Random;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class RandomLocalityTaskFactory extends TaskFactory {
	private Random random;

	public RandomLocalityTaskFactory() {
		random = new Random();
	}

	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomLocalityMultiplicationTask(dep,
				Main.places.getPlaceID(random.nextInt(Main.places.length)), a,
				b, c, quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		return new RandomLocalityAdditionTask(dep,
				Main.places.getPlaceID(random.nextInt(Main.places.length)), a,
				b, c, quadrant);
	}
}

class RandomLocalityMultiplicationTask extends MultiplicationTask {
	public RandomLocalityMultiplicationTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

class RandomLocalityAdditionTask extends AdditionTask {
	public RandomLocalityAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		super(dep, placeID, new RandomLocalityTaskFactory(), a, b, c, quadrant);
	}
}

public class RandomLocalityBenchmark extends Benchmark {
	public RandomLocalityBenchmark() {
		super(new RandomLocalityTaskFactory());
	}
}
