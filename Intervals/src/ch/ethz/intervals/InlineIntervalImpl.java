package ch.ethz.intervals;

import java.util.Set;

import ch.ethz.hwloc.PlaceID;

final class InlineIntervalImpl<R> extends Interval {
	InlineTask<R> task;
	private R result;
	private Set<Throwable> errors;

	InlineIntervalImpl(String name, Current current, InlineTask<R> task) {
		this(name, current, task, null);
	}

	InlineIntervalImpl(String name, Current current, InlineTask<R> task,
			PlaceID placeID) {
		super(name, current, current.inter, Point.FLAG_SYNCHRONOUS, 1, 2, placeID);
		this.task = task;
	}

	@Override
	protected Set<Throwable> catchErrors(Set<Throwable> errors) {
		this.errors = errors;
		return null;
	}

	@Override
	public String toString() {
		return task.toString();
	}

	@Override
	protected void run() {
		result = task.run(this);
	}

	R readResultOrRethrowErrors() {
		if (errors != null)
			throw new RethrownException(errors);
		return result;
	}
}
