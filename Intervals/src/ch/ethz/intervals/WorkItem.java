package ch.ethz.intervals;

import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.ThreadPool.Worker;

abstract class WorkItem {
	abstract PlaceID getPlaceID();

	abstract void exec(Worker worker);
}
