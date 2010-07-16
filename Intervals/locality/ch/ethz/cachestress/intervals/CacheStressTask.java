package ch.ethz.cachestress.intervals;

import ch.ethz.cachestress.Worker;
import ch.ethz.hwloc.Place;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.ParentForNew;

public class CacheStressTask extends Interval {
	protected final int id;
	protected final int[] array;
	protected final Worker worker;

	public CacheStressTask(@ParentForNew("Parent") Dependency dep, Place place,
			int id, int[] array) {
		super(dep, "cache-stress-worker-" + id, place);
		this.id = id;
		this.array = array;
		worker = new Worker(id, array);
	}

	public void run() {
		worker.run();
	}
}