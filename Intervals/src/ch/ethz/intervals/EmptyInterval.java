package ch.ethz.intervals;

import ch.ethz.hwloc.PlaceID;

public class EmptyInterval extends Interval {
	public EmptyInterval(@ParentForNew("Parent") Dependency dep, String name) {
		this(dep, name, null);
	}

	public EmptyInterval(@ParentForNew("Parent") Dependency dep, String name,
			PlaceID placeID) {
		super(dep, name, placeID);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	protected void run() {
	}
}
