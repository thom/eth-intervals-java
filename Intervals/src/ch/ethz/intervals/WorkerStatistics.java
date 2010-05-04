package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Worker;

public class WorkerStatistics {
	public static final boolean ENABLED = Config.STATISTICS;

	private final Worker owner;

	// General statistics
	private AtomicInteger puts = new AtomicInteger(0);
	private AtomicInteger takeAttempts = new AtomicInteger(0);
	private AtomicInteger takeSuccesses = new AtomicInteger(0);
	private AtomicInteger takeFailures = new AtomicInteger(0);
	private AtomicInteger stealAttempts = new AtomicInteger(0);
	private AtomicInteger stealSuccesses = new AtomicInteger(0);
	private AtomicInteger stealFailures = new AtomicInteger(0);
	private AtomicInteger grows = new AtomicInteger(0);

	// Statistics for duplicating queues
	private AtomicInteger eagerExecutions = new AtomicInteger(0);
	private AtomicInteger workAttempts = new AtomicInteger(0);
	private AtomicInteger workSuccesses = new AtomicInteger(0);
	private AtomicInteger workFailures = new AtomicInteger(0);

	public WorkerStatistics(Worker owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;

		this.owner = owner;
	}

	// General statistics
	public void doPut() {
		assert ENABLED;
		puts.incrementAndGet();
	}

	public void doTakeAttempt() {
		assert ENABLED;
		takeAttempts.incrementAndGet();
	}

	public void doTakeSuccess() {
		assert ENABLED;
		takeSuccesses.incrementAndGet();
	}

	public void doTakeFailure() {
		assert ENABLED;
		takeFailures.incrementAndGet();
	}

	public void doStealAttempt() {
		assert ENABLED;
		stealAttempts.incrementAndGet();
	}

	public void doStealSuccess() {
		assert ENABLED;
		stealSuccesses.incrementAndGet();
	}

	public void doStealFailure() {
		assert ENABLED;
		stealFailures.incrementAndGet();
	}

	public void doGrow() {
		assert ENABLED;
		grows.incrementAndGet();
	}

	// Statistics for duplicating queues
	public void doEagerExecution() {
		assert ENABLED;
		eagerExecutions.incrementAndGet();
	}

	public void doWorkAttempt() {
		assert ENABLED;
		workAttempts.incrementAndGet();
	}

	public void doWorkSuccess() {
		assert ENABLED;
		workSuccesses.incrementAndGet();
	}

	public void doWorkFailure() {
		assert ENABLED;
		workFailures.incrementAndGet();
	}

	public String toString() {
		assert ENABLED;

		String result = "\nStatistics for " + owner + "\n\n";
		result += "Puts\n";
		result += puts + "\n\n";
		result += "Take Attempts\tTake Successes\tTake Failures\n";
		result += takeAttempts + "\t\t" + takeSuccesses + "\t\t" + takeFailures
				+ "\n\n";
		result += "Steal Attempts\tSteal Successes\tSteal Failures\n";
		result += stealAttempts + "\t\t" + stealSuccesses + "\t\t"
				+ stealFailures + "\n\n";
		result += "Deque Grows\n";
		result += grows + "\n";

		if (Config.DUPLICATING_QUEUE) {
			result += "\nWork Attempts\tWork Successes\tWork Failures\n";
			result += workAttempts + "\t\t" + workSuccesses + "\t\t"
					+ workFailures + "\n\n";
			result += "Eager Executions\n";
			result += eagerExecutions + "\n";
		}

		return result;
	}

	public void print() {
		assert ENABLED;
		System.err.println(this);
	}
}
