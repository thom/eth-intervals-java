package ch.ethz.intervals;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;

public class ExampleInterval extends Interval {
	public ExampleInterval(Dependency dep, String name) {
		super(dep, name);
	}

	protected void run() {
		System.out.println(toString());
	}

	public static void main(String[] args) {
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval start) {
				Interval a = new ExampleInterval(start, "a");
				Interval b = new ExampleInterval(start, "b");
				Interval c = new ExampleInterval(start, "c");
				Interval d = new ExampleInterval(start, "d");				

				Intervals.addHb(a, b);
				Intervals.addHb(a, c);
				Intervals.addHb(b, d);
				Intervals.addHb(c, d);
				Intervals.schedule();
			}
		});
	}
}
