package ch.ethz.sor;

import java.util.Random;

public class JGFSORBench extends SOR implements JGFSection2 {
	private int size;
	private int dataSizes[] = { 1000, 1500, 2000 };
	private static final int JACOBI_NUM_ITER = 100;
	private static final long RANDOM_SEED = 10101010;
	public static int numberOfThreads;
	public boolean failed; // NDM

	Random R = new Random(RANDOM_SEED);

	public JGFSORBench(int nthreads) {
		numberOfThreads = nthreads;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void initialize() {

	}

	public void runKernel() {
		double g[][] = randomMatrix(dataSizes[size], dataSizes[size], R);
		SORrun(1.25, g, JACOBI_NUM_ITER);
	}

	public void validate() {
		double refVal[] = { 0.498574406322512, 1.1234778980135105,
				1.9954895063582696 };
		double dev = Math.abs(gTotal - refVal[size]);
		if (dev > 1.0e-12) {
			System.out.println("Validation failed");
			System.out.println("Gtotal = " + gTotal + "  " + dev + "  " + size);
			failed = true; // NDM
		}
	}

	public void tidyUp() {
		System.gc();
	}

	public void run(int size) {
		JGFInstrumentor.addTimer("Section2:SOR:Kernel", "Iterations", size);

		setSize(size);
		initialize();
		runKernel();
		validate();
		tidyUp();

		JGFInstrumentor.addOpsToTimer("Section2:SOR:Kernel",
				(double) (JACOBI_NUM_ITER));

		JGFInstrumentor.printTimer("Section2:SOR:Kernel");
	}

	private static double[][] randomMatrix(int m, int n, java.util.Random r) {
		double a[][] = new double[m][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = r.nextDouble() * 1e-6;
			}
		}
		
		return a;
	}

}
