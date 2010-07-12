package ch.ethz.util;

public abstract class LocalityBenchmark {
	public abstract long run();

	protected void cleanJvm() {
		System.runFinalization();
		System.gc();
	}
}
