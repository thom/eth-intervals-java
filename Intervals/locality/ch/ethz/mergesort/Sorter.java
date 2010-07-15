package ch.ethz.mergesort;

import java.util.Arrays;
import java.util.Random;

public class Sorter {
	public final int id;
	public final int size;

	public Sorter(int id, int size) {
		this.id = id;
		this.size = size;
	}

	public Integer[] run() {
		Integer[] result = new Integer[size];
		Random random = new Random();

		// Fill array
		for (int i = 0; i < size; i++) {
			result[i] = random.nextInt(Main.upperBound);
		}

		// Sort array
		Arrays.sort(result);

		return result;
	}
}
