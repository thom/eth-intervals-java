package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

public class DuplicatingQueue implements WorkStealingQueue {
	private final Worker owner;
	private final static int size = 1024;
	private WorkItem[] tasks = new WorkItem[size];
	private int tailMin = Integer.MAX_VALUE;
	private volatile int tail = 0;
	private volatile int head = 0;

	public DuplicatingQueue(Worker owner) {
		this.owner = owner;
	}

	@Override
	public void put(WorkItem task) {
		assert task != null;

		// queue not full and no index overflow?
		if ((tail < Math.min(tailMin, head) + size)
				&& (tail < Integer.MAX_VALUE / 2)) {
			tasks[tail % size] = task;
			tail = tail + 1;
			if (WorkerStatistics.ENABLED)
				owner.stats.doPut();
		} else {
			synchronized (this) {
				if (head > tailMin)
					head = tailMin;
				tailMin = Integer.MAX_VALUE;

				// adjust the indices to prevent overflow
				int count = Math.max(0, tail - head);
				head = head % size;
				tail = head + count;
			}

			if (WorkerStatistics.ENABLED)
				owner.stats.doEagerExecution();

			// just run this task eagerly
			task.exec(owner);
		}
	}

	@Override
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		tail = tail - 1;
		WorkItem task = null;

		// can we pop safely?
		if (head <= Math.min(tailMin, tail)) {
			if (tailMin > tail)
				tailMin = tail;
			task = tasks[tail % size];
			tasks[tail % size] = null;
		} else {
			synchronized (this) {
				// adjust head and reset tailMin
				if (head > tailMin)
					head = tailMin;
				tailMin = Integer.MAX_VALUE;

				// try to pop again
				if (head <= tail) {
					task = tasks[tail % size];
					tasks[tail % size] = null;
				} else {
					tail = tail + 1; // restore tail when empty
				}
			}
		}

		if (WorkerStatistics.ENABLED) {
			if (task == null)
				owner.stats.doTakeFailure();
			else
				owner.stats.doTakeSuccess();
		}

		return task;
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		synchronized (this) {
			if (head < tail) {
				WorkItem task = tasks[head % size];
				head = head + 1;
				return task;
			} else {
				return null;
			}
		}
	}

}
