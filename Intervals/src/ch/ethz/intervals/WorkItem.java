package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

abstract class WorkItem {
	abstract void exec(Worker worker);
}
