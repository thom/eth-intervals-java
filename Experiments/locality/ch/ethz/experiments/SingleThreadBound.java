package ch.ethz.experiments;

import java.util.Arrays;
import java.util.Random;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

public class SingleThreadBound {
	public static void main(String[] args) throws SetAffinityException {
		Affinity.set(0);
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
