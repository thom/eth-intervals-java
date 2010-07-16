package ch.ethz.matmult.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.matmult.Matrix;
import ch.ethz.matmult.Quadrant;

public abstract class TaskFactory {
	protected abstract MultiplicationTask createMultiplicationTask(
			Dependency dep, Place place, Matrix a, Matrix b, Matrix c,
			Quadrant quadrant);

	protected abstract AdditionTask createAdditionTask(Dependency dep,
			Place place, Matrix a, Matrix b, Matrix c, Quadrant quadrant);
}
