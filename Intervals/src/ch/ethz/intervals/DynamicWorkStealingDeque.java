package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicStampedReference;

import ch.ethz.intervals.ThreadPool.Worker;

public class DynamicWorkStealingDeque implements WorkStealingQueue {
	class Node {
		static final int SIZE = 9;
		WorkItem[] tasks = new WorkItem[SIZE];
		Node next, prev;
	}

	@Override
	public void put(WorkItem task) {
		// TODO Auto-generated method stub

	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkItem take() {
		// TODO Auto-generated method stub
		return null;
	}

}
