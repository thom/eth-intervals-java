package ch.ethz.intervals;


public class Intervals {
	
	static class NamedTask extends AbstractTask {
		public final String name;
		public NamedTask(String name) {
			super();
			this.name = name;
		}
		public void run(Point currentEnd) {
		}
		public String toString() {
			return name;
		}		
	}
	
	/** Convenient task that does nothing. */
	public static final Task emptyTask = new NamedTask("emptyTask");
	static final Task readTask = new NamedTask("readTask");

	static final PointImpl ROOT_END = new PointImpl(1); // never occurs
	
	static final ThreadPool POOL = new ThreadPool();
	
	/** 
	 * Returns {@code i.start()} unless {@code i} is null, 
	 * in which case just returns null. */
	public static Point start(Interval i) {
		if(i == null)
			return null;
		return i.start();
	}
	
	/** 
	 * Returns {@code i.end()} unless {@code i} is null, 
	 * in which case just returns null. */
	public static Point end(Interval i) {
		if(i == null)
			return null;
		return i.end();
	}
	
	/** 
	 * Creates and returns a new unscheduled interval with no bound.
	 * You can add additional dependencies by invoking methods on the
	 * {@code UnscheduledInterval} object.  To obtain the new interval,
	 * it must be scheduled using {@link UnscheduledInterval#schedule(Task)}.
	 * 
	 * Once scheduled, the resulting interval will execute in 
	 * parallel with the caller.  To create a blocking interval, use
	 * {@link #blockingInterval(Task)}.
	 * 
	 * @see #intervalWithBound(Point)
	 * @see #blockingInterval(Task)
	 */
	public static UnscheduledInterval interval() {
		return intervalWithBound(ROOT_END);
	}
	
	/** 
	 * Creates and returns a new unscheduled interval with a bound
	 * {@code bnd}.  A bound is effectively a dependency {@code endBefore(bnd)},
	 * but has additional ramifications when doing dynamic race detection.
	 * 
	 * You can add additional dependencies by invoking methods on the
	 * {@code UnscheduledInterval} object.  To obtain the new interval,
	 * it must be scheduled using {@link UnscheduledInterval#schedule(Task)}.
	 *  
	 * Once scheduled, the resulting interval will execute in 
	 * parallel with the caller.  To create a blocking interval, use
	 * {@link #blockingInterval(Task)}.
	 * 
	 * @see #interval()
	 * @see #intervalDuring(Interval)
	 * @see #blockingInterval(Task)
	 */
	public static UnscheduledInterval intervalWithBound(Point bnd) {
		return new UnscheduledIntervalImpl((PointImpl) bnd);
	}
	
	/** 
	 * Creates and returns a new unscheduled interval with a bound
	 * {@code interval.end()} and which always starts after
	 * {@code interval.start()}.
	 * 
	 * You can add additional dependencies by invoking methods on the
	 * {@code UnscheduledInterval} object.  To obtain the new interval,
	 * it must be scheduled using {@link UnscheduledInterval#schedule(Task)}.
	 *  
	 * Once scheduled, the resulting interval will execute in 
	 * parallel with the caller.  To create a blocking interval, use
	 * {@link #blockingInterval(Task)}.
	 * 
	 * @see #interval()
	 * @see #intervalWithBound(Point) 
	 * @see #blockingInterval(Task)
	 */
	public static UnscheduledInterval intervalDuring(Interval interval) {
		return intervalWithBound(interval.end()).startAfter(interval.start());
	}
	
	/**
	 * @see AsyncPoint
	 */
	public static AsyncPoint asyncPoint(Point bound, int cnt) {
		checkCurrentIntervalEndHbOrSame(bound);
		PointImpl boundImpl = (PointImpl) bound;
		boundImpl.addWaitCount();
		return new AsyncPointImpl(boundImpl, cnt);
	}
	
	/**
	 * If set to false, disables all safety checks against
	 * cycles or race conditions.  
	 */
	public static final boolean SAFETY_CHECKS = true;	

	static void checkEdge(Point from, Point to) {
		if (SAFETY_CHECKS && !from.hb(to))
			throw new NoEdgeException(from, to);
	}
	
	static void checkEdgeOrSame(Point from, Point to) {
		if(from != to)
			checkEdge(from, to);
	}

	static void checkCurrentIntervalEndHbOrSame(Point to) {
		if(SAFETY_CHECKS) {
			Current cur = Current.get();
			checkEdgeOrSame(cur.end, to);
		}
	}

	/** Waits for {@code ep} to complete and returns its result.
	 *  Resets the currentInterval afterwards. */
	static void join(PointImpl pnt) {
		if(Debug.ENABLED)
			Debug.join(pnt);
		pnt.join();
		pnt.checkAndRethrowPendingException();
	}
	
	/**
	 * Waits for {@code pnt} to occur and returns its result, 
	 * possibly rethrowing any exception.  
	 * There must be a path from {@code pnt} to the end point
	 * of the current interval.
	 * 
	 * <b>Note:</b> Exceptions that occur in {@code task} are 
	 * wrapped in {@link RethrownException} and rethrown immediately,
	 * but may also propagate upwards depending on whether 
	 * {@code ep} was configured to mask errors with 
	 * {@link UnscheduledInterval#setMaskExceptions(boolean)}.
	 */
	public static void blockOn(Point pnt) {
		Current cur = Current.get();
		checkEdge(pnt, cur.end);
		join((PointImpl) pnt);
	}
	
	/**
	 * Creates a new interval which executes during the current interval.
	 * This interval will execute {@code task}.  This function does not
	 * return until the new interval has completed.
	 * 
	 * <b>Note:</b> Exceptions that occur in {@code task} are 
	 * wrapped in {@link RethrownException} and rethrown immediately.
	 * Exceptions never propagate to the current interval.
	 */
	public static void blockingInterval(Task task) 
	{
		Current current = Current.get();
		PointImpl end = (PointImpl) intervalWithBound(current.end)
			.setMaskExceptions(true)
			.schedule(task)
			.end();
		join(end);
	}
	
	/** 
	 * Returns the point which represents the end of the entire
	 * computation.  This point will not occur until all other
	 * points have occurred, and it is the only point without a bound. */
	public static Point rootEnd() {
		return ROOT_END;
	}

}
