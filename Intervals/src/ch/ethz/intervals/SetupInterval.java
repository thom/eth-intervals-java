package ch.ethz.intervals;

import static ch.ethz.intervals.Intervals.addHb;
import ch.ethz.hwloc.PlaceID;

/**
 * Base class for tasks with an extended setup period.
 *
 * @see #setup(Point, Interval)
 */
public abstract class SetupInterval extends Interval {
	public SetupInterval(@ParentForNew("Parent") Dependency dep) {
		super(dep);
	}

	public SetupInterval(@ParentForNew("Parent") Dependency dep, PlaceID placeID) {
		super(dep, placeID);
	}

	public SetupInterval(@ParentForNew("Parent") Dependency dep, String name) {
		super(dep, name);
	}

	public SetupInterval(@ParentForNew("Parent") Dependency dep, String name,
			PlaceID placeID) {
		super(dep, name, placeID);
	}

	@Override
	public void run() {
		new Interval(this, "setup") {
			protected void run() {
				final Point setupEnd = end;

				class WorkerInterval extends Interval {

					public WorkerInterval() {
						super(SetupInterval.this, "worker");
						addHb(setupEnd, start);
					}

					@Override
					protected void run() {
					}

					@Override
					public String toString() {
						return "worker";
					}

				}

				WorkerInterval worker = new WorkerInterval();
				worker.schedule();

				setup(setupEnd, worker);
			}

			@Override
			public String toString() {
				return "setup";
			}
		};
	}

	/**
	 * Overriden to define the behavior of this task. This method defines the
	 * setup period. The worker interval parameter will not execute until after
	 * the end of the setup period, so you can create subintervals of
	 * {@code worker} that will not run until this method returns.
	 * 
	 * Normally, however, this guarantee can be obtained in a more
	 * straight-forward fashion by any task simply by not invoking
	 * {@link Intervals#schedule()} until all setup is done (or allowing the
	 * runtime to invoke it automatically for you).
	 */
	protected abstract void setup(Point setupEnd, Interval worker);
}
