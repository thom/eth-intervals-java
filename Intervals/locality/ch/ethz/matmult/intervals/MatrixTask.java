package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class MatrixTask extends Interval {
	protected final TaskFactory factory;
	protected final Matrix a, b, c;
	protected final Quadrant quadrant;

	public MatrixTask(@ParentForNew("Parent") Dependency dep, PlaceID placeID,
			String name, TaskFactory factory, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant) {
		super(dep, name, placeID);
		this.factory = factory;
		this.a = a;
		this.b = b;
		this.c = c;
		this.quadrant = quadrant;
	}

	protected MultiplicationTask createMultiplicationTask(Dependency dep,
			PlaceID placeID, Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return factory.createMultiplicationTask(dep, placeID, a, b, c, quadrant);
	}

	protected AdditionTask createAdditionTask(Dependency dep, PlaceID placeID,
			Matrix a, Matrix b, Matrix c, Quadrant quadrant) {
		return factory.createAdditionTask(dep, placeID, a, b, c, quadrant);
	}
}
