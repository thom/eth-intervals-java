package ch.ethz.cachestress;

public class Worker {
	public final int id;
	public final int array[];

	public Worker(int id, int[] array) {
		this.id = id;
		this.array = array;
	}

	public void run() {
		for (int k = 0; k < 100; k++) {
			// Sum up array slice
			int sum = 0;
			for (int i = 0; i < array.length; i++) {
				sum += array[i];
			}
			// Multiply array slice
			int mult = 1;
			for (int i = 0; i < array.length; i++) {
				mult *= array[i];
			}
		}
	}
}
