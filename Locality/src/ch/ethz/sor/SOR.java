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
			threadObjects[i] = new SORRunner(i, omega, g, numberOfIterations,
					sync);
			threads[i] = new Thread(threadObjects[i]);
			threads[i].start();
		}

		threadObjects[0] = new SORRunner(0, omega, g, numberOfIterations, sync);
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

	private static long[][] initSync(int nthreads) {
		long sync[][] = new long[JGFSORBench.numberOfThreads][cachelineSize];
		for (int i = 0; i < JGFSORBench.numberOfThreads; i++)
			sync[i][0] = 0;
		return sync;
	}

}

class SORRunner implements Runnable {
	int id, numberOfIterations;
	double g[][], omega;
	volatile long sync[][];

	public SORRunner(int id, double omega, double g[][],
			int numberOfIterations, long[][] sync) {
		this.id = id;
		this.omega = omega;
		this.g = g;
		this.numberOfIterations = numberOfIterations;
		this.sync = sync;
	}

	public void run() {
		int m = g.length;
		int n = g[0].length;

		double omegaOverFour = omega * 0.25;
		double oneMinusOmega = 1.0 - omega;

		// Update interior points
		int mm1 = m - 1;
		int nm1 = n - 1;

		int iLow, iUpper, slice, tSlice, ttSlice;

		tSlice = (mm1) / 2;
		ttSlice = (tSlice + JGFSORBench.numberOfThreads - 1)
				/ JGFSORBench.numberOfThreads;
		slice = ttSlice * 2;

		iLow = id * slice + 1;
		iUpper = ((id + 1) * slice) + 1;
		if (iUpper > mm1)
			iUpper = mm1 + 1;
		if (id == (JGFSORBench.numberOfThreads - 1))
			iUpper = mm1 + 1;

		for (int p = 0; p < 2 * numberOfIterations; p++) {
			for (int i = iLow + (p % 2); i < iUpper; i = i + 2) {
				double[] gI = g[i];
				double[] gIm1 = g[i - 1];

				if (i == 1) {
					double[] gIp1 = g[i + 1];

					for (int j = 1; j < nm1; j = j + 2) {
						gI[j] = omegaOverFour
								* (gIm1[j] + gIp1[j] + gI[j - 1] + gI[j + 1])
								+ oneMinusOmega * gI[j];

					}
				} else if (i == mm1) {
					double[] gIm2 = g[i - 2];

					for (int j = 1; j < nm1; j = j + 2) {
						if ((j + 1) != nm1) {
							gIm1[j + 1] = omegaOverFour
									* (gIm2[j + 1] + gI[j + 1] + gIm1[j] + gIm1[j + 2])
									+ oneMinusOmega * gIm1[j + 1];
						}
					}

				} else {
					double[] gIp1 = g[i + 1];
					double[] gIm2 = g[i - 2];

					for (int j = 1; j < nm1; j = j + 2) {
						gI[j] = omegaOverFour
								* (gIm1[j] + gIp1[j] + gI[j - 1] + gI[j + 1])
								+ oneMinusOmega * gI[j];

						if ((j + 1) != nm1) {
							gIm1[j + 1] = omegaOverFour
									* (gIm2[j + 1] + gI[j + 1] + gIm1[j] + gIm1[j + 2])
									+ oneMinusOmega * gIm1[j + 1];
						}
					}
				}

			}

			// Signal this thread has done iteration
			sync[id][0]++;

			// Wait for neighbors
			if (id > 0) {
				while (sync[id - 1][0] < sync[id][0]) {
				}
			}
			if (id < JGFSORBench.numberOfThreads - 1) {
				while (sync[id + 1][0] < sync[id][0]) {
				}
			}
		}
	}
}
