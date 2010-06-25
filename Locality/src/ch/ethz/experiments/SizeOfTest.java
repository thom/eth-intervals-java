package ch.ethz.experiments;

import ch.ethz.util.SizeOf;

public class SizeOfTest {
	public static void main(String[] args) {
		int i = 4194296;
		int j = 2097144;

		System.out.printf("int: %s\n", SizeOf.getHumanReadable(i));
		System.out.printf("int[%d]: %s\n", i, SizeOf
				.getHumanReadable(new int[i]));
		System.out.printf("int[%d]: %s\n", i, SizeOf.get(new int[i]));
		System.out.printf("int[%d]: %s\n", j, SizeOf
				.getHumanReadable(new int[j]));
		System.out.printf("int[%d]: %s\n", j, SizeOf.get(new int[j]));
		System.out.printf("Integer[%d]: %s\n", j, SizeOf
				.getHumanReadable(new Integer[j]));
		System.out.printf("Integer[%d]: %s\n", j, SizeOf.get(new Integer[j]));
	}
}
