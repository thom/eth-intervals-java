package ch.ethz.intervals;

import ch.ethz.hwloc.*;

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
	public final static WorkStealingQueue createQueue() {
		return new LinkedBlockingWorkStealingDeque();
	}
}
