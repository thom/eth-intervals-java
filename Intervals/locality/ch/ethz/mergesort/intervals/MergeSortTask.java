package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.ParentForNew;

public abstract class MergeSortTask extends Interval {
	protected final int id;
	protected Integer[] array;

	public MergeSortTask(@ParentForNew("Parent") Dependency dep, Place place,
			String name, int id) {
		super(dep, place);
		this.id = id;
	}
}
