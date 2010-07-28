package ch.ethz.intervals;

import java.util.concurrent.LinkedBlockingDeque;

import ch.ethz.intervals.ThreadPool.Worker;

public class LinkedBlockingWorkStealingDeque extends
		LinkedBlockingDeque<WorkItem> implements WorkStealingQueue {
	private static final long serialVersionUID = 1L;

	@Override
	public void put(WorkItem task) {
		super.addLast(task);
	}

	@Override
	public WorkItem take() {
		return super.pollLast();
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		return super.pollFirst();
	}
}
