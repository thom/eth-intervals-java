package ch.ethz.intervals;

import ch.ethz.intervals.quals.Creator;

@ParentForNew @Creator("this.(ch.ethz.intervals.quals.Constructor)")
public interface Dependency {
	
	/**
	 * Returns the bound for a new interval when {@code this}
	 * is used as the interval's {@link Dependency} parameter.
	 * 
	 * <p>End-users should not override or implement this method.  
	 * Doing so can violate the race-freedom guarantees of our compiler.
	 */
	public Interval parentForNewInterval();
	
	/**
	 * Adds any additional edges required when a new interval 
	 * is constructed with {@code this} as the interval's 
	 * {@link Dependency} parameter.
	 * 
	 * <p>End-users should not override or implement this method.  
	 * Doing so can violate the race-freedom guarantees of our compiler.
	 */
	public void addHbToNewInterval(Interval inter);

}
