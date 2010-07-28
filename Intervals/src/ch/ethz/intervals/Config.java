package ch.ethz.intervals;

import ch.ethz.hwloc.*;
import ch.ethz.intervals.ThreadPool.Place;

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
	 * Creation method to simplify switching of work-stealing queue
	 * implementations
	 * 
	 * @param owner
	 * @return WorkStealingDeque
	 */
	public final static WorkStealingQueue createQueue(Place owner) {
		return new LinkedBlockingWorkStealingDeque(owner);
	}
}
