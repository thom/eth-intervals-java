package ch.ethz.mergesort;

public abstract class MergingWorker extends MergeSortWorker {
	private int id;
	private MergeSortWorker left;
	private MergeSortWorker right;
	private int[] array;

	public MergingWorker(int id, MergeSortWorker left, MergeSortWorker right) {
		super("merging-worker-" + id);
		this.id = id;
		this.left = left;
		this.right = right;
	}

	public int getWorkerId() {
		return id;
	}

	public int[] getArray() {
		return array;
	}

	public void run() {
		// Wait for left and right predecessor to finish
		try {
			left.join();
			right.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get sorted arrays
		int[] leftArray = left.getArray();
		int[] rightArray = right.getArray();

		// Initialize merged array
		array = new int[leftArray.length + rightArray.length];

		// Merge sorted arrays
		int i = 0, j = 0, k = 0;

		while (i < leftArray.length && j < rightArray.length) {
			if (leftArray[i] < rightArray[j]) {
				array[k] = leftArray[i];
				i++;
			} else {
				array[k] = rightArray[j];
				j++;
			}
			k++;
		}

		if (i == leftArray.length) {
			for (int jj = j; jj < rightArray.length; jj++) {
				array[k] = rightArray[jj];
				k++;
			}
		} else {
			for (int ii = i; ii < leftArray.length; ii++) {
				array[k] = leftArray[ii];
				k++;
			}
		}
	}
}