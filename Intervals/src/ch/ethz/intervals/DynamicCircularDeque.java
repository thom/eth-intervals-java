package ch.ethz.intervals;

public class DynamicCircularDeque {
	static class CircularArray {
		private int logLength;
		private WorkItem[] workItems;

		CircularArray(int logLength) {
			this.logLength = logLength;
			workItems = new WorkItem[1 << logLength];
		}

		public int length() {
			return 1 << logLength;
		}

		WorkItem get(int i) {
			return workItems[i % length()];
		}

		void put(int i, WorkItem item) {
			workItems[i % length()] = item;
		}

		CircularArray grow(int bottom, int top) {
			CircularArray newWorkItems = new CircularArray(logLength + 1);
			for (int i = top; i < bottom; i++) {
				newWorkItems.put(i, get(i));
			}
			return newWorkItems;
		}
	}
}
