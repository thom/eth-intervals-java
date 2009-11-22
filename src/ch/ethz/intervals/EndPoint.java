package ch.ethz.intervals;

public interface EndPoint<V> extends Point {
	
	/**
	 * Returns the result which was generated by the task of the
	 * interval for which {@code this} is the end point.  
	 * If the interval's task function ended with an exception,
	 * that exception is rethrown here but wrapped in {@link RethrownException}.
	 * 
	 * @exception NoEdgeException if {@code this} does not <i>happen before</i> the
	 * start of the current interval.  This prevents data races, because we only
	 * allow access to the result of an interval if we know that it will have finished
	 * by the time the current interval is permitted to execute.
	 * 
	 * @exception RethrownException if the interval's task threw an exception
	 * rather than completing normally.
	 */
	V result();

}
