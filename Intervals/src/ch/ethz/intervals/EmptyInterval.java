package ch.ethz.intervals;

import ch.ethz.hwloc.Place;

public class EmptyInterval extends Interval {
	public EmptyInterval(@ParentForNew("Parent") Dependency dep, String name) {
		this(dep, name, null);
	}

	public EmptyInterval(@ParentForNew("Parent") Dependency dep, String name,
			Place place) {
		super(dep, name, place);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	protected void run() {
	}
}
