package ch.ethz.intervals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.ParentForNew;
import ch.ethz.intervals.VoidInlineTask;

public class ForkJoin {
	static final int N = 22;

	public static void main(String[] args) {
		final List<Integer> list = Collections
				.synchronizedList(new ArrayList<Integer>());

		// Interval task instance: defines the behavior of an interval,
		// like a Runnable. This particular instance just adds the number
		// "i" to "list".
		class AddTask extends Interval {
			final int i;

			public AddTask(@ParentForNew("Parent") Dependency dep, int i) {
				super(dep);
				this.i = i;
			}

			public void run() {
				list.add(i);
			}
		}

		// Create a new synchronous interval "current"
		// and spin off N independent children intervals,
		// each executing AddTask. Synchronous intervals
		// are children of the current interval and the current
		// interval always waits for them to finish before
		// proceeding.
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				for (int i = 0; i < N; i++) {
					new AddTask(subinterval, i);
				}
			}
		});

		// At this point, all of those intervals should have finished!
		list.add(-1);

		System.out.println("List size: " + list.size() + "\n");
		System.out.println("List content:\n");
		for (int i : list) {
			System.out.println(i);
		}
	}
}
