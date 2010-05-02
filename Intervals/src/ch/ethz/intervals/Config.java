package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

public class Config {
	// TODO: STATISTICS

	// TODO: DUPLICATING_QUEUE

	public final static WorkStealingQueue createQueue(Worker owner) {
		return new LazyDeque(owner);
	}
}
