package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicStampedReference;

import ch.ethz.intervals.ThreadPool.Worker;

public class DynamicWorkStealingDeque implements WorkStealingQueue {
	class Node {
		static final int SIZE = 1024;
		WorkItem[] tasks = new WorkItem[SIZE];
		Node next, prev;
	}

	class Index {
		Node node;
		int index;

		Index(Node node, int index) {
			this.node = node;
			this.index = index;
		}
	}

	private Index bottom;
	private AtomicStampedReference<Index> top;
	private final Worker owner;

	public DynamicWorkStealingDeque(Worker owner) {
		this.owner = owner;

		Node node1 = new Node();
		Node node2 = new Node();
		node1.next = node2;
		node2.prev = node1;

		bottom = new Index(node1, Node.SIZE - 1);
		top = new AtomicStampedReference<Index>(
				new Index(node1, Node.SIZE - 1), 0);
	}

	@Override
	public void put(WorkItem task) {
		// Read bottom data
		Node currentNode = bottom.node;
		int currentIndex = bottom.index;

		// Write data in current bottom cell
		currentNode.tasks[currentIndex] = task;

		Node newNode;
		int newIndex;
		if (currentIndex != 0) {
			newNode = currentNode;
			newIndex = currentIndex - 1;
		} else {
			// Allocate and link a new node
			newNode = new Node();
			newNode.next = currentNode;
			currentNode.prev = newNode;
			newIndex = Node.SIZE - 1;

			if (WorkerStatistics.ENABLED)
				owner.stats.doGrow();
		}

		// Update bottom
		bottom.node = newNode;
		bottom.index = newIndex;

		if (WorkerStatistics.ENABLED) {
			owner.stats.doPut();
		}
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		WorkItem task;

		while (true) {
			// Read top
			AtomicStampedReference<Index> currentTopRef = top;
			Index currentTop = currentTopRef.getReference();
			Node currentTopNode = currentTop.node;
			int currentTopIndex = currentTop.index;
			int currentTopTag = currentTopRef.getStamp();

			if (currentTopNode == null) {
				return null;
			}

			// Read bottom
			Index currentBottom = bottom;

			if (isEmpty(currentBottom, currentTop)) {
				if (currentTopRef == top) {
					return null;
				} else {
					// ABORT
					continue;
				}
			}

			// New top values
			int newTopTag;
			Node newTopNode;
			int newTopIndex;

			// If deque isn't empty, calculate next top pointer
			if (currentTopIndex != 0) {
				// Stay at current node
				newTopTag = currentTopTag;
				newTopNode = currentTopNode;
				newTopIndex = currentTopIndex - 1;
			} else {
				// Move to next node and update tag
				newTopTag = currentTopTag + 1;
				newTopNode = currentTopNode.prev;
				newTopIndex = Node.SIZE - 1;
			}

			// Read value
			task = currentTopNode.tasks[currentTopIndex];

			// New top
			Index newTop = new Index(newTopNode, newTopIndex);

			// Try to update top using CAS
			if (top.compareAndSet(currentTop, newTop, currentTopTag, newTopTag)) {
				break;
			}
		}

		return task;
	}

	@Override
	public WorkItem take() {
		// Read bottom data
		Node oldBottomNode = bottom.node;
		int oldBottomIndex = bottom.index;

		Node newBottomNode;
		int newBottomIndex;
		if (oldBottomIndex != Node.SIZE - 1) {
			newBottomNode = oldBottomNode;
			newBottomIndex = oldBottomIndex + 1;
		} else {
			newBottomNode = oldBottomNode.next;
			newBottomIndex = 0;
		}

		if (newBottomNode == null) {
			return null;
		}

		// Read data to be taken
		WorkItem task = newBottomNode.tasks[newBottomIndex];

		// Update bottom
		bottom.node = newBottomNode;
		bottom.index = newBottomIndex;

		// Read top
		AtomicStampedReference<Index> currentTopRef = top;
		Index currentTop = currentTopRef.getReference();
		Node currentTopNode = currentTop.node;
		int currentTopIndex = currentTop.index;
		int currentTopTag = currentTopRef.getStamp();

		// Case 1: If top has crossed bottom
		if ((oldBottomNode == currentTopNode)
				&& (newBottomIndex == currentTopIndex)) {
			// Return bottom to its old position
			bottom.node = oldBottomNode;
			bottom.index = oldBottomIndex;
			return null;
		}
		// Case 2: When taking the last entry in the deque (i.e. deque is empty
		// after the update of bottom)
		else if ((newBottomNode == currentTopNode)
				&& (newBottomIndex == currentTopIndex)) {
			// Try to update top's tag so no concurrent steal operation will
			// also take the same entry
			Index newTop = new Index(currentTopNode, currentTopIndex);
			if (top.compareAndSet(currentTop, newTop, currentTopTag,
					currentTopTag + 1)) {
				return task;
			}
			// If CAS failed (i.e. a concurrent steal operation already took the
			// last entry)
			else {
				// Return bottom to its old position
				bottom.node = oldBottomNode;
				bottom.index = oldBottomIndex;
				return null;
			}
		}
		// Case 3: Regular case (i.e. there was at least one entry in the deque
		// after bottom's update)
		else {
			return task;
		}
	}

	private boolean isEmpty(Index currentBottom, Index currentTop) {
		if (currentTop == null)
			System.out.println("currentTop: " + currentTop);

		// Same node
		if (currentBottom.node == currentTop.node) {
			// Same cell
			if (currentBottom.index == currentTop.index)
				return true;

			// Simple crossing
			if (currentTop.index - currentBottom.index == 1)
				return true;
		}
		// Neighboring nodes
		else if (currentBottom.node.prev == currentTop.node) {
			// Simple crossing
			if ((currentTop.index == 0)
					&& (currentBottom.index == Node.SIZE - 1))
				return true;
		}

		// Not empty
		return false;
	}

}
