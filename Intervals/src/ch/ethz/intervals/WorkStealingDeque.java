package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.intervals.ThreadPool.Worker;

public class WorkStealingDeque implements WorkStealingQueue {
	private volatile WorkItem[] tasks = new WorkItem[1024];
	private volatile int bottom = 0;
	private AtomicInteger top = new AtomicInteger(0);
	private final Worker owner;

	public WorkStealingDeque(Worker owner) {
		this.owner = owner;
	}

	@Override
	public void put(WorkItem task) {
		int oldBottom = bottom;
		int oldTop = top.get();
		WorkItem[] currentTasks = tasks;
		int size = oldBottom - oldTop;
		if (size >= currentTasks.length - 1) {
			currentTasks = expand(currentTasks, oldBottom, oldTop);
			tasks = currentTasks;
		}
		currentTasks[oldBottom % currentTasks.length] = task;
		bottom = oldBottom + 1;

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		int oldTop, oldBottom;
		WorkItem task;
		
		while (true) {
			// important that top read before bottom
			oldTop = top.get();
			oldBottom = bottom;
			WorkItem[] currentTasks = tasks;
			int size = oldBottom - oldTop;
			
			if (size <= 0)
				return null; // empty
			
			task = currentTasks[oldTop % currentTasks.length];
			
			if (top.compareAndSet(oldTop, oldTop + 1)) // fetch and increment
				break;
		}
		
		return task;
	}

	@Override
	public WorkItem take() {
		int oldBottom = this.bottom;
		WorkItem[] currentTasks = tasks;
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

		WorkItem task = currentTasks[bottom % currentTasks.length];

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

	private WorkItem[] expand(WorkItem[] currentTasks, int bottom, int top) {
		if (WorkerStatistics.ENABLED)
			owner.stats.doGrow();

		WorkItem[] newTasks = new WorkItem[currentTasks.length * 2];

		for (int i = top; i < bottom; i++) {
			newTasks[i % newTasks.length] = currentTasks[i
					% currentTasks.length];
		}

		return newTasks;
	}

}
