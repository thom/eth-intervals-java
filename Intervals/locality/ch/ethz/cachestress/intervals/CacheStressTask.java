package ch.ethz.cachestress.intervals;

import ch.ethz.cachestress.Task;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.ParentForNew;

public class CacheStressTask extends Interval {
	protected final int id;
	protected final int[] array;
	protected final Task task;

	public CacheStressTask(@ParentForNew("Parent") Dependency dep, PlaceID placeID,
			int id, int[] array) {
		super(dep, "cache-stress-task-" + id, placeID);
		this.id = id;
		this.array = array;
		task = new Task(id, array);
	}

	public void run() {
		task.run();
	}
}