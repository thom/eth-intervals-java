package ch.ethz.experiments;

import ch.ethz.util.SizeOf;

public class SizeOfTest {
	public static void main(String[] args) {
		int i = 1800000;
		int array[] = new int[i];
		for (int j = 0; j < array.length; j++) {
			array[j] = j;
		}

		System.out.printf("int: %s\n", SizeOf.getHumanReadable(i));
		System.out.printf("int[%d]: %s\n", i, SizeOf
				.getHumanReadable(new int[i]));
	}
}
