package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Main;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

class BestLocalityTaskFactory extends TaskFactory {
	@Override
	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		PlaceID placeID = null;

		// Diagonal:
		// * 0, 3 -> place 0
		// * 1, 2 -> place 1
		//
		// Row:
		// * 0, 1 -> place 0
		// * 2, 3 -> place 1
		//
		// Column
		// * 0, 2 -> place 0
		// * 1, 3 -> place 1
		switch (quadrant) {
		case Quadrant0:
		case Quadrant3:
			placeID = Main.places.getPlaceID(0);
			break;
		case Quadrant1:
		case Quadrant2:
			placeID = Main.places.getPlaceID(1);
			break;
		}

		return new BestLocalityMultiplicationTask(dep, placeID, a, b, c,
				quadrant);
	}

	@Override
	protected AdditionTask createAdditionTask(Dependency dep, Matrix a,
			Matrix b, Matrix c, Quadrant quadrant) {
		PlaceID placeID = null;

		// Diagonal:
		// * 0, 3 -> place 0
		// * 1, 2 -> place 1
		//
		// Row:
		// * 0, 1 -> place 0
		// * 2, 3 -> place 1
		//
		// Column
		// * 0, 2 -> place 0
		// * 1, 3 -> place 1
		switch (quadrant) {
		case Quadrant0:
		case Quadrant3:
			placeID = Main.places.getPlaceID(0);
			break;
		case Quadrant1:
		case Quadrant2:
			placeID = Main.places.getPlaceID(1);
			break;
		}

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
