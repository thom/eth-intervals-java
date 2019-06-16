package ch.ethz.util;

import org.kohsuke.args4j.Option;

/**
 * Specifies the command line values
 */
public abstract class CommandLineValues {
	@Option(name = "-m", aliases = { "--machine" }, usage = "machine name, options: Mafushi (default), Marvin")
	private Machine machine = Machine.Mafushi;

	@Option(name = "-t", aliases = { "--type" }, usage = "benchmark type, options: threads (default), threadpool, intervals, single", metaVar = "TYPE")
	private BenchmarkType type = BenchmarkType.threads;

	@Option(name = "-l", aliases = { "--locality" }, usage = "locality, options: Best (default), Ignorant, Random, Worst, (BestCol, BestDiag, BestRow, RandomCore, RandomPlace)", metaVar = "LOCALITY")
	private Locality locality = Locality.Best;

	@Option(name = "-p", aliases = { "--threads" }, usage = "number of threads to use in the threadpool, default value: cores of machine (only used when type is threadpool)")
	private int threads = 0;

	@Option(name = "-r", aliases = { "--runs" }, usage = "number of runs, default value: 10")
	private int runs = 10;

	@Option(name = "-k", aliases = { "--kbest" }, usage = "k-best, must be <= runs, default value: 3")
	private int kbest = 3;

	public Machine getMachine() {
		return machine;
	}

	public BenchmarkType getType() {
		return type;
	}

	public Locality getLocality() {
		return locality;
	}

	public int getThreads() {
		return threads;
	}

	public int getRuns() {
		return runs;
	}

	public int getKbest() {
		return kbest;
	}
}
