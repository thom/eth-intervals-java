package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Place;

// TODO: Finish PlaceStatistics
public class PlaceStatistics {
	public static final boolean ENABLED = Config.STATISTICS;

	private final Place owner;

	public PlaceStatistics(Place owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;

		this.owner = owner;
	}
}
