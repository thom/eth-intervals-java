package ch.ethz.intervals;

import java.util.concurrent.LinkedBlockingDeque;

import ch.ethz.intervals.ThreadPool.Place.Worker;

public class LinkedBlockingWorkStealingDeque extends
		LinkedBlockingDeque<WorkItem> implements WorkStealingQueue {
	private final Worker owner;

	private static final long serialVersionUID = 1L;

	public LinkedBlockingWorkStealingDeque(Worker owner) {
		this.owner = owner;
	}

	@Override
	public void put(WorkItem task) {
		super.addLast(task);

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem take() {
		WorkItem task = super.pollLast();

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
		return super.pollFirst();
	}
}
