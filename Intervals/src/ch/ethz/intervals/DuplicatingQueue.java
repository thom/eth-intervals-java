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
		if (task == null)
			return;

		// queue not full and no index overflow?
		if ((tail < Math.min(tailMin, head) + size)
				&& (tail < Integer.MAX_VALUE / 2)) {
			tasks[tail % size] = task;
			tail = tail + 1;
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

			// just run this task eagerly
			task.exec(owner);
		}
	}

	@Override
	public WorkItem take() {
		tail = tail - 1;

		// can we pop safely?
		if (head <= Math.min(tailMin, tail)) {
			if (tailMin > tail)
				tailMin = tail;
			WorkItem task = tasks[tail % size];
			tasks[tail % size] = null;
			return task;
		} else {
			synchronized (this) {
				// adjust head and reset tailMin
				if (head > tailMin)
					head = tailMin;
				tailMin = Integer.MAX_VALUE;

				// try to pop again
				if (head <= tail) {
					WorkItem task = tasks[tail % size];
					tasks[tail % size] = null;
					return task;

				} else {
					tail = tail + 1; // restore tail when empty
					return null;
				}
			}
		}
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
