package ch.ethz.mergesort;

public class Merger {
	public final int id;
	public final Integer[] leftArray;
	public final Integer[] rightArray;

	public Merger(int id, Integer[] leftArray, Integer[] rightArray) {
		this.id = id;
		this.leftArray = leftArray;
		this.rightArray = rightArray;
	}

	public Integer[] run() {
		Integer[] result = new Integer[leftArray.length + rightArray.length];

		// Merge sorted arrays
		int i = 0, j = 0, k = 0;

		while (i < leftArray.length && j < rightArray.length) {
			if (leftArray[i] < rightArray[j]) {
				result[k] = leftArray[i];
				i++;
			} else {
				result[k] = rightArray[j];
				j++;
			}
			k++;
		}

		if (i == leftArray.length) {
			for (int jj = j; jj < rightArray.length; jj++) {
				result[k] = rightArray[jj];
				k++;
			}
		} else {
			for (int ii = i; ii < leftArray.length; ii++) {
				result[k] = leftArray[ii];
				k++;
			}
		}

		return result;
	}
}
