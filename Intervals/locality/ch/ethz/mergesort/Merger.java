package ch.ethz.mergesort;

public class Merger implements MergeSorter {
	public final int id;
	public final Integer[] leftArray;
	public final Integer[] rightArray;

	private Integer[] array;

	public Merger(int id, Integer[] leftArray, Integer[] rightArray) {
		this.id = id;
		this.leftArray = leftArray;
		this.rightArray = rightArray;
	}

	public Integer[] getArray() {
		return array;
	}

	public void run() {
		array = new Integer[leftArray.length + rightArray.length];

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
