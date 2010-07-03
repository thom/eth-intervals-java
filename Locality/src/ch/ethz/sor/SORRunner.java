package ch.ethz.sor;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.MafushiUnits;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.hwloc.Units;

public class SORRunner implements Runnable {
	int id, numberOfIterations;
	double g[][], omega;
	volatile long sync[][];
	Units units;

	public SORRunner(int id, double omega, double g[][],
			int numberOfIterations, long[][] sync) {
		this.id = id;
		this.omega = omega;
		this.g = g;
		this.numberOfIterations = numberOfIterations;
		this.sync = sync;
		this.units = new MafushiUnits();
	}

	public void run() {
		try {
			Affinity.set(units.get(id));
		} catch (SetAffinityException e) {
			e.printStackTrace();
		}

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