package ch.ethz.util;

import java.util.Random;

public abstract class LocalityBenchmark {
	protected StopWatch stopWatch;

	public abstract long run();

	protected void startBenchmark() {
		stopWatch = new StopWatch();
		cleanJvm();
		stopWatch.start();
	}

	protected long stopBenchmark() {
		stopWatch.stop();
		cleanJvm();
		return stopWatch.getElapsedTime();
	}

	protected void cleanJvm() {
		System.runFinalization();
		System.gc();
	}

	protected int[] createRandomIntegerArray(int size) {
		Random random = new Random();
		int[] tmp = new int[size];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = random.nextInt();
		}

		return tmp;
	}
}
