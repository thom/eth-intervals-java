package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.mergesort.Sorter;

public class SortingTask extends MergeSortTask {
	protected final int size;

	public SortingTask(@ParentForNew("Parent") Dependency dep, Place place,
			int id, int size) {
		super(dep, place, "sorting-worker-", id);
		this.size = size;
	}

	public void run() {
		Sorter sorter = new Sorter(id, size);
		sorter.run();
		array = sorter.getArray();
	}
}
