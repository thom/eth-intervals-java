package ch.ethz.sor;

public class SOR {
	public static double gTotal = 0.0;
	public static final int cachelineSize = 128;
	public static volatile long sync[][];

	public static final void SORrun(double omega, double g[][],
			int numberOfIterations) {
		SORRunner threadObjects[] = new SORRunner[JGFSORBench.numberOfThreads];
		Thread threads[] = new Thread[JGFSORBench.numberOfThreads];
		sync = initSync(JGFSORBench.numberOfThreads);

		JGFInstrumentor.startTimer("Section2:SOR:Kernel");

		for (int i = 1; i < JGFSORBench.numberOfThreads; i++) {
			threadObjects[i] = createSORRunner(i, omega, g, numberOfIterations);
			threads[i] = new Thread(threadObjects[i]);
			threads[i].start();
		}

		threadObjects[0] = createSORRunner(0, omega, g, numberOfIterations);
		threadObjects[0].run();

		for (int i = 1; i < JGFSORBench.numberOfThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		}

		JGFInstrumentor.stopTimer("Section2:SOR:Kernel");

		int length = g[0].length - 1;
		for (int i = 1; i < length; i++) {
			for (int j = 1; j < length; j++) {
				gTotal += g[i][j];
			}
		}

	}

	private static SORRunner createSORRunner(int i, double omega, double g[][],
			int numberOfIterations) {
		return new SORRunner(i, omega, g, numberOfIterations, sync);
	}

	private static long[][] initSync(int nthreads) {
		long sync[][] = new long[JGFSORBench.numberOfThreads][cachelineSize];
		for (int i = 0; i < JGFSORBench.numberOfThreads; i++)
			sync[i][0] = 0;
		return sync;
	}
}