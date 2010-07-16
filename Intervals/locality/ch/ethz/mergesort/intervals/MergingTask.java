package ch.ethz.mergesort.intervals;

import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.mergesort.Merger;

public class MergingTask extends MergeSortTask {
	protected final MergeSortTask left;
	protected final MergeSortTask right;

	public MergingTask(@ParentForNew("Parent") Dependency dep, Place place,
			int id, MergeSortTask left, MergeSortTask right) {
		super(dep, place, "merging-worker-", id);
		this.left = left;
		this.right = right;
	}

	public void run() {
		// Create merger
		Merger merger = new Merger(id, left.array, right.array);

		// Initialize merged array
		merger.run();
		array = merger.getArray();
	}
}