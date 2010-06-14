package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

public class Config {
	/**
	 * Should we collect and print statistics
	 */
	public static final boolean STATISTICS = false;

	/**
	 * Are we using a duplicating queue?
	 *
	 * Use state (init, running, done) to make sure tasks are executed only
	 * once.
	 */
	public static final boolean DUPLICATING_QUEUE = false;

	/**
	 * Creation method to simplify switching of work-stealing queue
	 * implementations
	 *
	 * @param owner
	 * @return WorkStealingDeque
	 */
	public final static WorkStealingQueue createQueue(Worker owner) {
		return new DynamicCircularDequeRTA(owner);
	}
}
