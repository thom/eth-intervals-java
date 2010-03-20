package ch.ethz.intervals;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;

public class HB extends Interval {
	public HB(Dependency dep, String name) {
		super(dep, name);
	}

	@Override
	protected void run() {
		System.out.println(toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				Interval a = new HB(subinterval, "a");
				Interval b = new HB(subinterval, "b");
				Interval c = new HB(subinterval, "c");

				Intervals.addHb(a, b);
				Intervals.addHb(b, c);
			}
		});
	}
}
