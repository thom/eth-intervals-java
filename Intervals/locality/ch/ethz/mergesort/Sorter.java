package ch.ethz.mergesort;

import java.util.Arrays;
import java.util.Random;

public class Sorter implements MergeSorter {
	public final int id;
	public final int size;

	private Integer[] array;

	public Sorter(int id, int size) {
		this.id = id;
		this.size = size;
	}

	public Integer[] getArray() {
		return array;
	}

	public void run() {
		array = new Integer[size];
		Random random = new Random();

		// Fill array
		for (int i = 0; i < size; i++) {
			array[i] = random.nextInt(Main.upperBound);
		}

		// Sort array
		Arrays.sort(array);
	}
}
