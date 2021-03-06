package ch.ethz.intervals;

import ch.ethz.intervals.quals.DefinesGhost;

/** Ghost field linking an interval to its parent. */
@DefinesGhost(ofClass=Interval.class, useByDefault=false)
public @interface Parent {
	public String value() default "";
}
