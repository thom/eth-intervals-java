package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicReferenceArray;

import ch.ethz.intervals.ThreadPool.Place.Worker;

// Called "Lazy" because the owner of the deque only lazily updates the
// location of the head of deque, i.e., only when it tries to pop something
// and finds it gone
public class WorkStealingLazyDeque implements WorkStealingQueue {
	static class ThiefData {
		int head = 0;
	}

	private AtomicReferenceArray<WorkItem> tasks = new AtomicReferenceArray<WorkItem>(
			1024);

	int ownerHead = 0, ownerTail = 0;

	private final ThiefData thief = new ThiefData();

	private final Worker owner;

	public WorkStealingLazyDeque(Worker owner) {
		this.owner = owner;
	}

	@Override
	public void put(WorkItem task) {
		assert task != null;

		while (true) {
			final int length = tasks.length();
			final int tail = ownerTail;

			// Would be full or would roll-over
			if (tail - ownerHead >= length || tail == Integer.MAX_VALUE) {
				expand();
				continue;
			}

			tasks.set(index(length, tail), task);
			ownerTail = tail + 1;

			if (WorkerStatistics.ENABLED)
				owner.stats.doPut();

			return;
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// At most one thief at a time
		synchronized (thief) {
			final int head = thief.head;
			final int index = index(head);

			WorkItem item = tasks.get(index);
			if (!tasks.compareAndSet(index, item, null)) {
				item = null;
			}

			// If null, was either already taken by owner or never there.
			if (item == null)
				return null;

			// Successfully stolen!
			thief.head++;
			return item;
		}
	}

	@Override
	// Only owner can take. Returns either NULL or a WorkItem that should be
	// executed.
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		// Empty
		if (ownerHead == ownerTail) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			return null;
		}

		// Pop the last item from the deque.
		final int lastTail = ownerTail - 1;
		final int lastIndex = index(lastTail);

		// Read the item popped.
		// Note: if we get back null, the item must have been stolen, since
		// otherwise we never store null into the array, and we know this
		// location was initialized.
		WorkItem item = tasks.get(lastIndex);
		if (!tasks.compareAndSet(lastIndex, item, null)) {
			item = null;
		}

		// Only updates the location of the head of the deque when it tries
		// to pop something and finds it gone
		if (item == null) {
			// The item we put here was stolen!
			// If this item was stolen, then all previous entries must have been
			// stolen too. Update our notion of the head of the deque.
			ownerHead = ownerTail;

			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();

			// Deque is now empty.
			return null;
		}

		ownerTail = lastTail;

		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeSuccess();

		return item;
	}

	private void expand() {
		// Only owner can expand, no thieves are active
		synchronized (thief) {
			assert ownerHead <= thief.head && thief.head <= ownerTail;
			ownerHead = thief.head;
			final int length = tasks.length();
			final int thold = length >> 4;
			final int size = (ownerTail - ownerHead);

			// Less than 1/16 is free.
			if ((length - size) <= thold) {
				replaceTaskArray(length * 2);
			}
			// About to roll-over.
			else if (ownerTail == Integer.MAX_VALUE) {
				replaceTaskArray(length);
			}
		}
	}

	private void replaceTaskArray(int size) {
		if (WorkerStatistics.ENABLED)
			owner.stats.doGrow();

		AtomicReferenceArray<WorkItem> newTasks = new AtomicReferenceArray<WorkItem>(
				size);

		final int length = tasks.length();
		int newTail = 0;

		for (int i = ownerHead; i < ownerTail; i++) {
			newTasks.set(index(size, newTail), tasks.get(index(length, i)));
			newTail++;
		}

		ownerTail = newTail;
		ownerHead = thief.head = 0;
		tasks = newTasks;
	}

	private final int index(int id) {
		return index(tasks.length(), id);
	}

	private final int index(int length, int id) {
		return id % length;
	}

}
