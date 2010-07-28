package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Place.Worker;

public class WorkerStatistics {
	public static final boolean ENABLED = Config.STATISTICS;

	private final Worker owner;

	// Global statistics
	// General
	static private AtomicInteger globalTakeAttempts = new AtomicInteger(0);
	static private AtomicInteger globalTakeSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalTakeFailures = new AtomicInteger(0);
	static private AtomicInteger globalStealAttempts = new AtomicInteger(0);
	static private AtomicInteger globalStealSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalStealFailures = new AtomicInteger(0);

	// Idle workers
	static private AtomicInteger globalIdleWorkersAdds = new AtomicInteger(0);
	static private AtomicInteger globalIdleWorkersRemoves = new AtomicInteger(0);

	// Duplicating queues
	static private AtomicInteger globalWorkAttempts = new AtomicInteger(0);
	static private AtomicInteger globalWorkSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalWorkFailures = new AtomicInteger(0);

	// Worker statistics
	// General
	private AtomicInteger takeAttempts = new AtomicInteger(0);
	private AtomicInteger takeSuccesses = new AtomicInteger(0);
	private AtomicInteger takeFailures = new AtomicInteger(0);
	private AtomicInteger stealAttempts = new AtomicInteger(0);
	private AtomicInteger stealSuccesses = new AtomicInteger(0);
	private AtomicInteger stealFailures = new AtomicInteger(0);

	// Idle workers
	private AtomicInteger idleWorkersAdds = new AtomicInteger(0);
	private AtomicInteger idleWorkersRemoves = new AtomicInteger(0);

	// Duplicating queues
	private AtomicInteger workAttempts = new AtomicInteger(0);
	private AtomicInteger workSuccesses = new AtomicInteger(0);
	private AtomicInteger workFailures = new AtomicInteger(0);

	public WorkerStatistics(Worker owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;
		this.owner = owner;
	}

	// General
	public void doTakeAttempt() {
		assert ENABLED;
		takeAttempts.incrementAndGet();
		globalTakeAttempts.incrementAndGet();
	}

	public void doTakeSuccess() {
		assert ENABLED;
		takeSuccesses.incrementAndGet();
		globalTakeSuccesses.incrementAndGet();
	}

	public void doTakeFailure() {
		assert ENABLED;
		takeFailures.incrementAndGet();
		globalTakeFailures.incrementAndGet();
	}

	public void doStealAttempt() {
		assert ENABLED;
		stealAttempts.incrementAndGet();
		globalStealAttempts.incrementAndGet();
	}

	public void doStealSuccess() {
		assert ENABLED;
		stealSuccesses.incrementAndGet();
		globalStealSuccesses.incrementAndGet();
	}

	public void doStealFailure() {
		assert ENABLED;
		stealFailures.incrementAndGet();
		globalStealFailures.incrementAndGet();
	}

	// Idle workers
	public void doIdleWorkersAdd() {
		assert ENABLED;
		idleWorkersAdds.incrementAndGet();
		globalIdleWorkersAdds.incrementAndGet();
	}

	public void doIdleWorkersRemove() {
		assert ENABLED;
		idleWorkersRemoves.incrementAndGet();
		globalIdleWorkersRemoves.incrementAndGet();
	}

	// Duplicating queues
	public void doWorkAttempt() {
		assert ENABLED;
		workAttempts.incrementAndGet();
		globalWorkAttempts.incrementAndGet();
	}

	public void doWorkSuccess() {
		assert ENABLED;
		workSuccesses.incrementAndGet();
		globalWorkSuccesses.incrementAndGet();
	}

	public void doWorkFailure() {
		assert ENABLED;
		workFailures.incrementAndGet();
		globalWorkFailures.incrementAndGet();
	}

	// Print
	public static String globalToString() {
		assert ENABLED;
		String result = "Gone Idle\tWoken up\n";
		result += globalIdleWorkersAdds + "\t\t" + globalIdleWorkersRemoves
				+ "\n\n";
		result += "Take Attempts\tTake Successes\tTake Failures\n";
		result += globalTakeAttempts + "\t\t" + globalTakeSuccesses + "\t\t"
				+ globalTakeFailures + "\n\n";
		result += "Steal Attempts\tSteal Successes\tSteal Failures\n";
		result += globalStealAttempts + "\t\t" + globalStealSuccesses + "\t\t"
				+ globalStealFailures + "\n";
		if (Config.DUPLICATING_QUEUE) {
			result += "\nWork Attempts\tWork Successes\tWork Failures\n";
			result += globalWorkAttempts + "\t\t" + globalWorkSuccesses
					+ "\t\t" + globalWorkFailures + "\n";
		}
		return result;
	}

	public static void globalPrint() {
		assert ENABLED;
		System.err.println(globalToString());
	}

	public String toString() {
		assert ENABLED;
		String result = "\nStatistics for " + owner + "\n\n";
		result += "Gone Idle\tWoken up\n";
		result += idleWorkersAdds + "\t\t" + idleWorkersRemoves + "\n\n";
		result += "Take Attempts\tTake Successes\tTake Failures\n";
		result += takeAttempts + "\t\t" + takeSuccesses + "\t\t" + takeFailures
				+ "\n\n";
		result += "Steal Attempts\tSteal Successes\tSteal Failures\n";
		result += stealAttempts + "\t\t" + stealSuccesses + "\t\t"
				+ stealFailures + "\n";
		if (Config.DUPLICATING_QUEUE) {
			result += "\nWork Attempts\tWork Successes\tWork Failures\n";
			result += workAttempts + "\t\t" + workSuccesses + "\t\t"
					+ workFailures + "\n";
		}
		return result;
	}

	public void print() {
		assert ENABLED;
		System.err.println(this);
	}
}
