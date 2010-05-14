package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicStampedReference;

import ch.ethz.intervals.ThreadPool.Worker;

public class IdempotentDeque implements WorkStealingQueue {
	class ArrayData {
		final int head, size;

		public ArrayData(int head, int size) {
			this.head = head;
			this.size = size;
		}
	}

	private final Worker owner;
	private AtomicStampedReference<ArrayData> anchor;
	private WorkItem[] tasks;

	public IdempotentDeque(Worker owner) {
		this.owner = owner;
		anchor = new AtomicStampedReference<ArrayData>(new ArrayData(0, 0), 0);
		tasks = new WorkItem[1024];
	}

	@Override
	public void put(WorkItem task) {
		// Order write in (1) before write in (2)
		int head, size, tag;

		while (true) {
			ArrayData arrayData = anchor.getReference();
			head = arrayData.head;
			size = arrayData.size;
			tag = anchor.getStamp();

			if (size == tasks.length) {
				expand();
			} else {
				break;
			}
		}

		// (1)
		tasks[(head + size) % tasks.length] = task;

		// (2)
		anchor.set(new ArrayData(head, size + 1), tag + 1);

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// Order read in (1) before read in (2)
		// Order read in (3) before CAS in (4)
		ArrayData arrayData;
		int head, size, tag;
		WorkItem task;

		while (true) {
			// (1)
			arrayData = anchor.getReference();
			head = arrayData.head;
			size = arrayData.size;
			tag = anchor.getStamp();

			if (size == 0) {
				return null;
			}

			// (2)
			WorkItem[] tempTasks = tasks;

			// (3)
			task = tempTasks[head % tempTasks.length];

			int newHead = head + 1 % Integer.MAX_VALUE;

			// (4)
			if (anchor.compareAndSet(arrayData,
					new ArrayData(newHead, size - 1), tag, tag)) {
				break;
			}
		}

		return task;
	}

	@Override
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		ArrayData arrayData = anchor.getReference();
		int head = arrayData.head;
		int size = arrayData.size;
		int tag = anchor.getStamp();

		if (size == 0) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			return null;
		}

		WorkItem task = tasks[(head + size - 1) % tasks.length];
		anchor.set(new ArrayData(head, size - 1), tag);

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

		ArrayData arrayData = anchor.getReference();
		int head = arrayData.head;
		int size = arrayData.size;

		// (1)
		WorkItem[] tempTasks = new WorkItem[2 * size];

		for (int i = 0; i < size; i++) {
			// (2)
			tempTasks[(head + i) % tempTasks.length] = tasks[(head + i)
					% tasks.length];
		}

		// (3)
		tasks = tempTasks;
	}

}
