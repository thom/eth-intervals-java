package ch.ethz.mergesort.global;

public abstract class MergingWorker extends MergeSortWorker {
	protected final MergeSortWorker left;
	protected final MergeSortWorker right;

	private int[] temp;

	public MergingWorker(int id, int[] sharedArray, MergeSortWorker left,
			MergeSortWorker right) {
		super("merging-worker-", id, sharedArray, left.start, right.end);
		this.left = left;
		this.right = right;
		temp = new int[sharedArray.length];
	}

	public void run() {
		// Wait for left and right predecessor threads to finish
		try {
			left.join();
			right.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Merge sorted arrays
		merge(left.start, left.end, right.start, right.end);
	}

	private void merge(int start1, int end1, int start2, int end2) {
		int leftpos = start1;
		int rightpos = start2;
		int pos = start1;

		while (leftpos < end1 && rightpos < end2) {
			if (sharedArray[leftpos] >= sharedArray[rightpos]) {
				temp[pos++] = sharedArray[rightpos++];
			} else if (sharedArray[rightpos] >= sharedArray[leftpos]) {
				temp[pos++] = sharedArray[leftpos++];
			}
		}

		// Append sharedArray[start2 : end2] to temp
		if (leftpos >= end1) {
			System.arraycopy(sharedArray, rightpos, temp, pos, end2 - rightpos);
		}

		// Append sharedArray[start1 : end1] to temp
		if (rightpos >= end2) {
			System.arraycopy(sharedArray, leftpos, temp, pos, end1 - leftpos);
		}

		// Copy temp to sharedArray at respective positions
		System.arraycopy(temp, start1, sharedArray, start1, end2 - start1);
	}
}