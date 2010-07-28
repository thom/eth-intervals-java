package ch.ethz.intervals;

import java.util.concurrent.LinkedBlockingDeque;

import ch.ethz.intervals.ThreadPool.Place;
import ch.ethz.intervals.ThreadPool.Place.Worker;

public class LinkedBlockingWorkStealingDeque extends
		LinkedBlockingDeque<WorkItem> implements WorkStealingQueue {
	private final Place owner;

	private static final long serialVersionUID = 1L;

	public LinkedBlockingWorkStealingDeque(Place owner) {
		this.owner = owner;
	}

	@Override
	public void put(WorkItem task) {
		super.addLast(task);

		if (PlaceStatistics.ENABLED) {
			owner.stats.doPut();
		}
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
