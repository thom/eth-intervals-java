package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicStampedReference;

import ch.ethz.intervals.ThreadPool.Place.Worker;

public class IdempotentLIFOQueue implements WorkStealingQueue {
	private final Worker owner;
	private AtomicStampedReference<Integer> anchor;
	private int capacity;
	private WorkItem[] tasks;

	public IdempotentLIFOQueue(Worker owner) {
		this.owner = owner;
		anchor = new AtomicStampedReference<Integer>(0, 0);
		capacity = 1024;
		tasks = new WorkItem[capacity];
	}

	@Override
	public void put(WorkItem task) {
		// Order write in (1) before write in (2)
		int tail, tag;

		while (true) {
			tail = anchor.getReference();
			tag = anchor.getStamp();
			if (tail == capacity) {
				expand();
			} else {
				break;
			}
		}

		// (1)
		tasks[tail] = task;

		// (2)
		anchor.set(tail + 1, tag + 1);

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// Order read in (1) before read in (2)
		// Order read in (3) before CAS in (4)
		int tail, tag;
		WorkItem task;

		while (true) {
			// (1)
			tail = anchor.getReference();
			tag = anchor.getStamp();

			// (2)
			if (tail == 0) {
				return null;
			}

			WorkItem[] tempTasks = tasks;

			// (3)
			task = tempTasks[tail - 1];

			// (4)
			if (anchor.compareAndSet(tail, tail - 1, tag, tag)) {
				break;
			}
		}

		return task;
	}

	@Override
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		int tail = anchor.getReference();
		int tag = anchor.getStamp();

		if (tail == 0) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			return null;
		}

		WorkItem task = tasks[tail - 1];
		anchor.set(tail - 1, tag);

		if (WorkerStatistics.ENABLED) {
			if (task == null)
				owner.stats.doTakeFailure();
			else
				owner.stats.doTakeSuccess();
		}

		return task;
	}

	private void expand() {
		// Order writes in (1) before writes in (2)
		// Order write in (2) before write in put:(2)

		if (WorkerStatistics.ENABLED)
			owner.stats.doGrow();

		WorkItem[] tempTasks = new WorkItem[2 * capacity];

		// (1)
		for (int i = 0; i < capacity; i++) {
			tempTasks[i] = tasks[i];
		}

		// (2)
		tasks = tempTasks;

		capacity = 2 * capacity;
	}

}