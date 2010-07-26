package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Place.Worker;

public class DynamicCircularDequeRTA implements WorkStealingQueue {
	private final static int INITIAL_LOG_CAPACITY = 10;
	private volatile CircularArray tasks = new CircularArray(
			INITIAL_LOG_CAPACITY);
	private volatile int bottom = 0;
	private int lazyTop = 0;
	private AtomicInteger top = new AtomicInteger(0);
	private final Worker owner;

	public DynamicCircularDequeRTA(Worker owner) {
		this.owner = owner;
	}

	public void put(WorkItem task) {
		int oldBottom = bottom;
		CircularArray currentTasks = tasks;
		int size = oldBottom - lazyTop;
		if (size >= currentTasks.length() - 1) {
			lazyTop = top.get();
			currentTasks = currentTasks.grow(oldBottom, lazyTop);
			tasks = currentTasks;
		}
		currentTasks.put(oldBottom, task);
		bottom = oldBottom + 1;

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	public WorkItem take() {
		int oldBottom = this.bottom;
		CircularArray currentTasks = tasks;
		oldBottom = oldBottom - 1;
		this.bottom = oldBottom;
		int oldTop = top.get();
		int size = oldBottom - oldTop;

		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		if (size < 0) {
			bottom = oldTop;

			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			return null;
		}

		WorkItem task = currentTasks.get(bottom);

		if (size > 0) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeSuccess();
			return task;
		}

		if (!top.compareAndSet(oldTop, oldTop + 1)) // fetch and increment
			task = null; // queue is empty

		bottom = oldTop + 1;

		if (WorkerStatistics.ENABLED) {
			if (task == null)
				owner.stats.doTakeFailure();
			else
				owner.stats.doTakeSuccess();
		}

		return task;
	}

	public WorkItem steal(Worker thiefWorker) {
		// important that top read before bottom
		int oldTop = top.get();
		int oldBottom = bottom;
		CircularArray currentTasks = tasks;
		int size = oldBottom - oldTop;
		if (size <= 0)
			return null; // empty
		WorkItem task = currentTasks.get(oldTop);
		if (!top.compareAndSet(oldTop, oldTop + 1)) // fetch and increment
			return null; // abort
		return task;
	}

	class CircularArray {
		private int logLength;
		private WorkItem[] workItems;

		CircularArray(int logLength) {
			this.logLength = logLength;
			workItems = new WorkItem[1 << logLength];
		}

		public int length() {
			return 1 << logLength;
		}

		WorkItem get(int i) {
			return workItems[i % length()];
		}

		void put(int i, WorkItem item) {
			workItems[i % length()] = item;
		}

		CircularArray grow(int bottom, int top) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doGrow();

			CircularArray newWorkItems = new CircularArray(logLength + 1);
			for (int i = top; i < bottom; i++) {
				newWorkItems.put(i, get(i));
			}
			return newWorkItems;
		}
	}
}
