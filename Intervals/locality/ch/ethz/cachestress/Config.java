package ch.ethz.cachestress;

import ch.ethz.hwloc.*;

public class Config {
	public static Units units = new MafushiUnits();
	public static final int ARRAY_SIZE = 2097144;
	public static final int RUNS = 10;
	public static final int K_BEST = 3;
	public static String name = "cache stress";
	public static String packageName = "ch.ethz.cachestress";
}
