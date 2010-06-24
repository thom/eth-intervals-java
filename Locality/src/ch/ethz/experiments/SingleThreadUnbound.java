package ch.ethz.experiments;

import java.util.Arrays;
import java.util.Random;

public class SingleThreadUnbound {
	public static void main(String[] args) {
		int[] array = createRandomIntegerArray(100000000);
		Arrays.sort(array);
	}

	private static int[] createRandomIntegerArray(int size) {
		Random random = new Random();
		int[] tmp = new int[size];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = random.nextInt();
		}

		return tmp;
	}
}
