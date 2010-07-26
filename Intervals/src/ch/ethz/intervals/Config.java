package ch.ethz.intervals;

import ch.ethz.hwloc.*;
import ch.ethz.intervals.ThreadPool.Place.Worker;

public class Config {
	/**
	 * Creation method to simplify switching to another machine
	 */
	public static final Places places = new MafushiPlaces();

	/**
	 * Should workers be bound to cores?
	 */
	public static final boolean AFFINITY = true;

	/**
	 * Should we collect and print statistics?
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
		return new LinkedBlockingWorkStealingDeque(owner);
	}
}
