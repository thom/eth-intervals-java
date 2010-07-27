package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Place.Worker;

// TODO: Move over queue statistics to PlaceStatistics
public class WorkerStatistics {
	public static final boolean ENABLED = Config.STATISTICS;

	private final Worker owner;

	// Global statistics
	// General
	static private AtomicInteger globalPuts = new AtomicInteger(0);
	static private AtomicInteger globalTakeAttempts = new AtomicInteger(0);
	static private AtomicInteger globalTakeSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalTakeFailures = new AtomicInteger(0);
	static private AtomicInteger globalStealAttempts = new AtomicInteger(0);
	static private AtomicInteger globalStealSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalStealFailures = new AtomicInteger(0);
	static private AtomicInteger globalGrows = new AtomicInteger(0);

	// Pending work
	static private AtomicInteger globalPendingWorkItemsAdds = new AtomicInteger(
			0);
	static private AtomicInteger globalPendingWorkItemsRemoveAttempts = new AtomicInteger(
			0);
	static private AtomicInteger globalPendingWorkItemsRemoves = new AtomicInteger(
			0);

	// Idle workers
	static private AtomicInteger globalIdleWorkersAdds = new AtomicInteger(0);
	static private AtomicInteger globalIdleWorkersRemoves = new AtomicInteger(0);

	// Duplicating queues
	static private AtomicInteger globalEagerExecutions = new AtomicInteger(0);
	static private AtomicInteger globalWorkAttempts = new AtomicInteger(0);
	static private AtomicInteger globalWorkSuccesses = new AtomicInteger(0);
	static private AtomicInteger globalWorkFailures = new AtomicInteger(0);

	// Worker statistics
	// General
	private AtomicInteger puts = new AtomicInteger(0);
	private AtomicInteger takeAttempts = new AtomicInteger(0);
	private AtomicInteger takeSuccesses = new AtomicInteger(0);
	private AtomicInteger takeFailures = new AtomicInteger(0);
	private AtomicInteger stealAttempts = new AtomicInteger(0);
	private AtomicInteger stealSuccesses = new AtomicInteger(0);
	private AtomicInteger stealFailures = new AtomicInteger(0);
	private AtomicInteger grows = new AtomicInteger(0);

	// Pending work
	private AtomicInteger pendingWorkItemsRemoveAttempts = new AtomicInteger(0);
	private AtomicInteger pendingWorkItemsRemoves = new AtomicInteger(0);

	// Idle workers
	private AtomicInteger idleWorkersAdds = new AtomicInteger(0);
	private AtomicInteger idleWorkersRemoves = new AtomicInteger(0);

	// Duplicating queues
	private AtomicInteger eagerExecutions = new AtomicInteger(0);
	private AtomicInteger workAttempts = new AtomicInteger(0);
	private AtomicInteger workSuccesses = new AtomicInteger(0);
	private AtomicInteger workFailures = new AtomicInteger(0);

	public WorkerStatistics(Worker owner) {
		// all statistics events should be protected by if(ENABLED)
		assert ENABLED;

		this.owner = owner;
	}

	// Global
	public static void doPendingWorkItemsAdd() {
		assert ENABLED;
		globalPendingWorkItemsAdds.incrementAndGet();
	}

	// General
	public void doPut() {
		assert ENABLED;
		puts.incrementAndGet();
		globalPuts.incrementAndGet();
	}

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

	public void doGrow() {
		assert ENABLED;
		grows.incrementAndGet();
		globalGrows.incrementAndGet();
	}

	// Pending work
	public void doPendingWorkItemsRemoveAttempt() {
		assert ENABLED;
		pendingWorkItemsRemoveAttempts.incrementAndGet();
		globalPendingWorkItemsRemoveAttempts.incrementAndGet();
	}

	public void doPendingWorkItemsRemove() {
		assert ENABLED;
		pendingWorkItemsRemoves.incrementAndGet();
		globalPendingWorkItemsRemoves.incrementAndGet();
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
	public void doEagerExecution() {
		assert ENABLED;
		eagerExecutions.incrementAndGet();
		globalEagerExecutions.incrementAndGet();
	}

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

	public static String globalToString() {
		assert ENABLED;
		String result = "\nGlobal Statistics\n\n";
		result += "Gone Idle\tWoken up\n";
		result += globalIdleWorkersAdds + "\t\t" + globalIdleWorkersRemoves
				+ "\n\n";
		result += "Puts\n";
		result += globalPuts + "\n\n";
		result += "Take Attempts\tTake Successes\tTake Failures\n";
		result += globalTakeAttempts + "\t\t" + globalTakeSuccesses + "\t\t"
				+ globalTakeFailures + "\n\n";
		result += "Steal Attempts\tSteal Successes\tSteal Failures\n";
		result += globalStealAttempts + "\t\t" + globalStealSuccesses + "\t\t"
				+ globalStealFailures + "\n\n";
		result += "Pending Work\n";
		result += "Adds\tRemove Attempts\tRemoves\n";
		result += globalPendingWorkItemsAdds + "\t"
				+ globalPendingWorkItemsRemoveAttempts + "\t\t"
				+ globalPendingWorkItemsRemoves + "\n\n";
		result += "Deque Grows\n";
		result += globalGrows + "\n";
		if (Config.DUPLICATING_QUEUE) {
			result += "\nWork Attempts\tWork Successes\tWork Failures\n";
			result += globalWorkAttempts + "\t\t" + globalWorkSuccesses
					+ "\t\t" + globalWorkFailures + "\n\n";
			result += "Eager Executions\n";
			result += globalEagerExecutions + "\n";
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
		result += "Puts\n";
		result += puts + "\n\n";
		result += "Take Attempts\tTake Successes\tTake Failures\n";
		result += takeAttempts + "\t\t" + takeSuccesses + "\t\t" + takeFailures
				+ "\n\n";
		result += "Steal Attempts\tSteal Successes\tSteal Failures\n";
		result += stealAttempts + "\t\t" + stealSuccesses + "\t\t"
				+ stealFailures + "\n\n";
		result += "Pending Work\n";
		result += "Remove Attempts\tRemoves\n";
		result += pendingWorkItemsRemoveAttempts + "\t\t"
				+ pendingWorkItemsRemoves + "\n\n";
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
