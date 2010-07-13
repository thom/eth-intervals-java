package ch.ethz.util;

import org.kohsuke.args4j.Option;

/**
 * Specifies the command line values
 */
public abstract class CommandLineValues {
	@Option(name = "-m", aliases = { "--machine" }, usage = "machine name (default: Mafushi)")
	private Machine machine = Machine.Mafushi;

	@Option(name = "-t", aliases = { "--type" }, usage = "benchmark type (default: threads)", metaVar = "TYPE")
	private BenchmarkType type = BenchmarkType.threads;

	@Option(name = "-l", aliases = { "--locality" }, usage = "locality (default: Best)", metaVar = "LOCALITY")
	private Locality locality = Locality.Best;

	@Option(name = "-r", aliases = { "--runs" }, usage = "number of runs (default: 10)")
	private int runs = 10;

	@Option(name = "-k", aliases = { "--kbest" }, usage = "k-best, must be <= runs (default: 3)")
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

	public int getRuns() {
		return runs;
	}

	public int getKbest() {
		return kbest;
	}
}
