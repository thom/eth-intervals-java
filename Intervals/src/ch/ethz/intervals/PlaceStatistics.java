package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Place;

public class PlaceStatistics {
	public static final boolean ENABLED = Config.STATISTICS;

	private final Place owner;

	// Global statistics
	static private AtomicInteger globalPuts = new AtomicInteger(0);

	// Queue statistics
	private AtomicInteger puts = new AtomicInteger(0);

	public PlaceStatistics(Place owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;
		this.owner = owner;
	}

	// Queue
	public void doPut() {
		assert ENABLED;
		puts.incrementAndGet();
		globalPuts.incrementAndGet();
	}

	// Print
	public static String globalToString() {
		assert ENABLED;
		String result = "\nGlobal Statistics\n\n";
		result += "Puts\n";
		result += globalPuts + "\n";
		return result;
	}

	public static void globalPrint() {
		assert ENABLED;
		System.err.println(globalToString());
	}

	public String toString() {
		assert ENABLED;
		String result = "\nStatistics for " + owner + "\n\n";
		result += "Puts\n";
		result += puts + "\n";
		return result;
	}

	public void print() {
		assert ENABLED;
		System.err.println(this);
	}
}
