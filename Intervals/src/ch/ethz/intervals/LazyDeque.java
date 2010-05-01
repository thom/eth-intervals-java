package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicReferenceArray;

import ch.ethz.intervals.ThreadPool.Worker;

// Called "Lazy" because the owner of the deque only lazily updates
// the location of the head of deque, i.e., only when it tries to
// pop something and finds it gone
class LazyDeque implements WorkStealingQueue {
	static class ThiefData {
		int head = 0;
	}

	static final int INITIAL_SIZE = (1 << 10);

	// Experiment with different memory layouts: Try spacing out the items
	// so that they fall on different cache lines. Often if you have
	// multiple threads writing to different memory locations that happen
	// to fall on the same cache line, you can experience quite a lot of
	// contention by the underlying hardware. So, for example, if one has
	// an array, and the stealing thread writes to location 0 while the
	// owner thread writes to location 1, one can get very bad performance
	// even though the two writes are to distinct locations.
	//
	// Never tuned this very much. Did some experiments and found there
	// wasn't enough stealing to cause a lot of problems, but I left it
	// in there for later. At one point it ran without problems even
	// if you used non-zero values for PAD and OFFSET.
	// How many bits to shift the index by. So, if PAD is 2 then index 0
	// goes to position OFFSET. Index 1 goes to position OFFSET + (1 << 2).
	// Index 2 goes to position OFFSET + (2 << 2), etc.
	static final int PAD = 0;

	// How many entries to skip at the beginning of the array.
	// This can be important because the length of the array is the very
	// first value in memory, so all accesses touch that beginning part.
	// Therefore it can be a good idea to skip the initial X entries.
	static final int OFFSET = 0;

	private AtomicReferenceArray<WorkItem> tasksArray = new AtomicReferenceArray<WorkItem>(
			size(INITIAL_SIZE));

	int ownerHead = 0, ownerTail = 0;

	private final ThiefData thief = new ThiefData();

	private final Worker owner;

	public LazyDeque(Worker owner) {
		this.owner = owner;
	}

	private int index(int id) {
		return index(tasksArray.length() >> PAD, id);
	}

	private static int size(int l) {
		return (l << PAD) + OFFSET;
	}

	private static int index(int l, int id) {
		return ((id % l) << PAD) + OFFSET;
	}

	// Only owner can put.
	public void put(WorkItem task) {
		assert task != null;
		while (true) {
			final int l = tasksArray.length() >> PAD;
			final int tail = ownerTail;
			if (tail - ownerHead >= l || tail == Integer.MAX_VALUE) {
				// Would be full or would roll-over
				expand();
				continue;
			}
			final int index = index(l, tail);
			tasksArray.set(index, task);
			ownerTail = tail + 1;
			if (Debug.ENABLED)
				Debug.dequePut(owner, l, ownerHead, ownerTail, tail, task);
			return;
		}
	}

	// Only owner can take.
	public WorkItem take() {
		// Only owner can take. Returns either NULL or a WorkItem that
		// should be executed.
		if (ownerHead == ownerTail)
			return null; // Empty.

		// Pop the last item from the deque.
		final int lastTail = ownerTail - 1;

		final int lastIndex = index(lastTail);

		// Read the item popped.
		// Note: if we get back null, the item must have been stolen, since
		// otherwise we never store null into the array, and we know this
		// location was initialized.
		WorkItem item = tasksArray.getAndSet(lastIndex, null);

		// Only updates the location of the head of the deque when it tries
		// to pop something and finds it gone (lazy deque)
		try {
			if (item == null) {
				// The item we put here was stolen!
				// If this item was stolen, then all previous entries
				// must have been stolen too. Update our notion of the head
				// of the deque.
				ownerHead = ownerTail;

				// Deque is now empty.
				return null;
			}
			ownerTail = lastTail;
			return item;
		} finally {
			if (Debug.ENABLED)
				Debug.dequeTake(owner, tasksArray.length() >> PAD, ownerHead,
						ownerTail, lastIndex, item);
		}
	}

	public WorkItem steal(Worker thiefWorker) {
		// At most one thief at a time.
		synchronized (thief) {
			final int head = thief.head;
			final int index = index(head);
			WorkItem item = tasksArray.getAndSet(index, null);
			if (Debug.ENABLED)
				Debug.dequeSteal(owner, thiefWorker, thief.head, index, item);

			// if null, was either already taken by owner or never there.
			if (item == null)
				return null;

			// Successfully stolen!
			thief.head++;
			return item;
		}
	}

	private void expand() {
		// Only owner can expand.
		// No thieves are active.
		synchronized (thief) {
			assert ownerHead <= thief.head && thief.head <= ownerTail;
			ownerHead = thief.head;
			int l = tasksArray.length() >> PAD, thold = l >> 4;
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
		final int l = tasksArray.length() >> PAD;
		int j = 0;
		for (int i = ownerHead; i < ownerTail; i++)
			newTasks.set(index(size, j++), tasksArray.get(index(l, i)));
		ownerTail = j;
		ownerHead = thief.head = 0;
		tasksArray = newTasks;
	}
}
