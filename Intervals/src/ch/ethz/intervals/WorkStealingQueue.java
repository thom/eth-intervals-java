package ch.ethz.intervals;

import ch.ethz.intervals.ThreadPool.Worker;

interface WorkStealingQueue {
	public void put(WorkItem task);

	public WorkItem take();

	public WorkItem steal(Worker thiefWorker);
}
