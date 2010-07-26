package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Place.Worker;

abstract class WorkItem {
	abstract void exec(Worker worker);
}
