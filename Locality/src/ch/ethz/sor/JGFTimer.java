package ch.ethz.sor;

public class JGFTimer {
	public String name;
	public String opName;
	public double time;
	public double opCount;
	public long calls;
	public int size = -1;

	private long startTime;
	private boolean on;

	public JGFTimer(String name, String opName) {
		this.name = name;
		this.opName = opName;
		reset();
	}

	public JGFTimer(String name, String opName, int size) {
		this.name = name;
		this.opName = opName;
		this.size = size;
		reset();
	}

	public JGFTimer(String name) {
		this(name, "");
	}

	public void start() {
		if (on)
			System.out.println("Warning timer " + name
					+ " was already turned on");
		on = true;
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		time += (double) (System.currentTimeMillis() - startTime) / 1000.;
		if (!on)
			System.out.println("Warning timer " + name + " wasn't turned on");
		calls++;
		on = false;
	}

	public void addOps(double count) {
		opCount += count;
	}

	public void addTime(double addedTime) {
		time += addedTime;
	}

	public void reset() {
		time = 0.0;
		calls = 0;
		opCount = 0;
		on = false;
	}

	public double perf() {
		return opCount / time;
	}

	public void longPrint() {
		System.out
				.println("Timer            Calls         Time(s)       Performance("
						+ opName + "/s)");
		System.out.println(name + "           " + calls + "           " + time
				+ "        " + this.perf());
	}

	public void print() {
		if (opName.equals("")) {
			System.out.println(name + "   " + time + " (s)");
		} else {

			switch (size) {
			case 0:
				System.out.println(name + ":SizeA" + "\t" + time + " (s) \t "
						+ (float) this.perf() + "\t" + " (" + opName + "/s)");
				break;
			case 1:
				System.out.println(name + ":SizeB" + "\t" + time + " (s) \t "
						+ (float) this.perf() + "\t" + " (" + opName + "/s)");
				break;
			case 2:
				System.out.println(name + ":SizeC" + "\t" + time + " (s) \t "
						+ (float) this.perf() + "\t" + " (" + opName + "/s)");
				break;
			default:
				System.out.println(name + "\t" + time + " (s) \t "
						+ (float) this.perf() + "\t" + " (" + opName + "/s)");
				break;
			}

		}
	}

	public void printPerf() {
		String name = this.name;

		// pad name to 40 characters
		while (name.length() < 40)
			name = name + " ";

		System.out.println(name + "\t" + (float) this.perf() + "\t" + " ("
				+ opName + "/s)");
	}

}
