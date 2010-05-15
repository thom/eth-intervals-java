package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicReferenceArray;

import ch.ethz.intervals.ThreadPool.Worker;

// Called "Lazy" because the owner of the deque only lazily updates
// the location of the head of deque, i.e., only when it tries to
// pop something and finds it gone
public class WorkStealingLazyDeque implements WorkStealingQueue {
	static class ThiefData {
		int head = 0;
	}

	static final int INITIAL_SIZE = (1 << 10);

	private AtomicReferenceArray<WorkItem> tasksArray = new AtomicReferenceArray<WorkItem>(
			size(INITIAL_SIZE));

	int ownerHead = 0, ownerTail = 0;

	private final ThiefData thief = new ThiefData();

	private final Worker owner;

	public WorkStealingLazyDeque(Worker owner) {
		this.owner = owner;
	}

	private int index(int id) {
		return index(tasksArray.length(), id);
	}

	private static int size(int l) {
		return l;
	}

	private static int index(int l, int id) {
		return id % l;
	}

	@Override
	public void put(WorkItem task) {
		assert task != null;
		while (true) {
			final int l = tasksArray.length();
			final int tail = ownerTail;

			if (tail - ownerHead >= l || tail == Integer.MAX_VALUE) {
				// Would be full or would roll-over
				expand();
				continue;
			}

			final int index = index(l, tail);
			tasksArray.set(index, task);
			ownerTail = tail + 1;

			if (WorkerStatistics.ENABLED)
				owner.stats.doPut();

			return;
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// At most one thief at a time.
		synchronized (thief) {
			final int head = thief.head;
			final int index = index(head);

			WorkItem item = tasksArray.get(index);
			if (!tasksArray.compareAndSet(index, item, null)) {
				item = null;
			}

			// if null, was either already taken by owner or never there.
			if (item == null)
				return null;

			// Successfully stolen!
			thief.head++;
			return item;
		}
	}

	@Override
	public WorkItem take() {
		if (WorkerStatistics.ENABLED)
			owner.stats.doTakeAttempt();

		// Only owner can take. Returns either NULL or a WorkItem that
		// should be executed.
		if (ownerHead == ownerTail) {
			if (WorkerStatistics.ENABLED)
				owner.stats.doTakeFailure();
			return null; // Empty.
		}

		// Pop the last item from the deque.
		final int lastTail = ownerTail - 1;
		final int lastIndex = index(lastTail);

		// Read the item popped.
		// Note: if we get back null, the item must have been stolen, since
		// otherwise we never store null into the array, and we know this
		// location was initialized.
		WorkItem item = tasksArray.get(lastIndex);
		if (!tasksArray.compareAndSet(lastIndex, item, null)) {
			item = null;
		}

		// Only updates the location of the head of the deque when it tries
		// to pop something and finds it gone (lazy deque)
		if (item == null) {
			// The item we put here was stolen!
			// If this item was stolen, then all previous entries
			// must have been stolen too. Update our notion of the head
			// of the deque.
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
		// Only owner can expand.
		// No thieves are active.
		synchronized (thief) {
			assert ownerHead <= thief.head && thief.head <= ownerTail;
			ownerHead = thief.head;
			int l = tasksArray.length(), thold = l >> 4;
			int size = (ownerTail - ownerHead);

			// Less than 1/16 is free.
			if ((l - size) <= thold) {
				replaceTaskArray(l * 2);
			}

			// About to roll-over.
			else if (ownerTail == Integer.MAX_VALUE) {
				replaceTaskArray(l);
			}
		}
	}

	private void replaceTaskArray(int size) {
		if (WorkerStatistics.ENABLED)
			owner.stats.doGrow();

		AtomicReferenceArray<WorkItem> newTasks = new AtomicReferenceArray<WorkItem>(
				size(size));
		final int l = tasksArray.length();
		int j = 0;
		for (int i = ownerHead; i < ownerTail; i++)
			newTasks.set(index(size, j++), tasksArray.get(index(l, i)));
		ownerTail = j;
		ownerHead = thief.head = 0;
		tasksArray = newTasks;
	}

}
