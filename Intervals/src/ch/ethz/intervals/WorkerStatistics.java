package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

public class WorkerStatistics {
	public static final boolean ENABLED = false;

	private final Worker owner;

	// TODO: Count grows, steals (attempt, successful, unsuccessful), puts,
	// takes (attempt, successful, unsuccessful)

	public WorkerStatistics(Worker owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;

		this.owner = owner;
	}

	public String toString() {
		assert ENABLED;

		String result = "Statistics for worker " + owner + "\n\n";
		result += "TODO";
		result += "\n\n";
		return result;
	}
	
	public void print() {
		assert ENABLED;
		System.err.println(this);
	}
}
