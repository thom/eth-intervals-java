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
