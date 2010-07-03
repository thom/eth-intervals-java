package ch.ethz.sor;

public class JGFSORBenchSizeC {
	public static int numberOfThreads;

	public static void main(String argv[]) {

		if (argv.length != 0) {
			numberOfThreads = Integer.parseInt(argv[0]);
		} else {
			System.out
					.println("The number of threads has not been specified, defaulting to 1");
			System.out.println("  ");
			numberOfThreads = 1;
		}

		JGFInstrumentor.printHeader(2, 2, numberOfThreads);

		JGFSORBench sor = new JGFSORBench(numberOfThreads);
		sor.run(2);
	}
}
