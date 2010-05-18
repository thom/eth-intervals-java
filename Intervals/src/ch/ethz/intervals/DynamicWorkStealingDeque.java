package ch.ethz.intervals;

import java.util.concurrent.atomic.AtomicStampedReference;

import ch.ethz.intervals.ThreadPool.Worker;

public class DynamicWorkStealingDeque implements WorkStealingQueue {
	class Node {
		static final int SIZE = 9;
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
		}

		// Update bottom
		bottom.node = newNode;
		bottom.index = newIndex;
	}

	@Override
	public WorkItem steal(Worker thiefWorker) {
		// Read top
		AtomicStampedReference<Index> currentTopRef = top;
		Index currentTop = currentTopRef.getReference();
		Node currentTopNode = currentTop.node;
		int currentTopIndex = currentTop.index;
		int currentTopTag = currentTopRef.getStamp();

		// Read bottom
		Index currentBottom = bottom;

		if (isEmpty(currentBottom, currentTop))
			return null;

		// New top values
		int newTopTag;
		Node newTopNode;
		int newTopIndex;

		// If deque isn't empty, calculate next top pointer
		if (currentTopIndex != 0) {
			// stay at current node
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
		WorkItem task = currentTopNode.tasks[currentTopIndex];

		// New top
		Index newTop = new Index(newTopNode, newTopIndex);

		// Try to update top using CAS
		if (currentTopRef.compareAndSet(currentTop, newTop, currentTopTag,
				newTopTag)) {
			return task;
		} else {
			return null;
		}
	}

	@Override
	public WorkItem take() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isEmpty(Index currentBottom, Index currentTop) {
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
		else if (currentTop.node.next == currentBottom.node) {
			// Simple crossing
			if ((currentTop.index == 0)
					&& (currentBottom.index == Node.SIZE - 1))
				return true;
		}

		// Not empty
		return false;
	}

}
