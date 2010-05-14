package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Worker;

public class IdempotentFIFOQueue implements WorkStealingQueue {
	private final Worker owner;
	private AtomicInteger head;
	private int tail;
	private WorkItem[] tasks;

	public IdempotentFIFOQueue(Worker owner) {
		this.owner = owner;
		head = new AtomicInteger(0);
		tail = 0;
		tasks = new WorkItem[1024];
	}

	@Override
	public void put(WorkItem task) {
		// Order write at (1) before write at (2)
		int oldHead, oldTail;

		while (true) {
			oldHead = head.get();
			oldTail = tail;
			if (oldTail == oldHead + tasks.length) {
				expand();
			} else {
				break;
			}
		}

		// (1)
		tasks[oldTail % tasks.length] = task;

		// (2)
		tail = oldTail + 1;

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// Order read in (1) before read in (2)
		// Order read in (1) before read in (3)
		// Order read in (4) before CAS in (5)
		int oldHead, oldTail;
		WorkItem task;

		while (true) {
			// (1)
			oldHead = head.get();

			// (2)
			oldTail = tail;

			if (oldHead == oldTail) {
				return null;
			}

			// (3)
			WorkItem[] tempTasks = tasks;

			// (4)
			task = tempTasks[oldHead % tempTasks.length];

			// (5)
			if (head.compareAndSet(oldHead, oldHead + 1)) {
				break;
			}
		}

		return task;
	}

	@Override
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		int oldHead = head.get();
		int oldTail = tail;

		if (oldHead == oldTail) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			return null;
		}

		WorkItem task = tasks[oldHead % tasks.length];

		head.set(oldHead + 1);

		if (WorkerStatistics.ENABLED) {
			if (task == null)
				owner.stats.doTakeFailure();
			else
				owner.stats.doTakeSuccess();
		}

		return task;
	}

	private void expand() {
		// Order writes in (1) and (2) before write in (3)
		// Order write in (3) before write in put:(2)

		if (WorkerStatistics.ENABLED)
			owner.stats.doGrow();

		int size = tasks.length;

		// (1)
		WorkItem[] tempTasks = new WorkItem[2 * size];

		for (int i = head.get(); i < tail; i++) {
			// (2)
			tempTasks[i % tempTasks.length] = tasks[i % tasks.length];
		}

		// (3)
		tasks = tempTasks;
	}

}
