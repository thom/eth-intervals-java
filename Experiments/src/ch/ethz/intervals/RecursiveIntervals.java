package ch.ethz.intervals;
import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;

public class RecursiveIntervals extends Interval {
	private int number;
	
	RecursiveIntervals(Dependency dep, int number) {
		super(dep);
		this.number = number;
	}

	@Override
	protected void run() {
		if (number > 0) {
			System.out.println(number);
			new RecursiveIntervals(parent, number - 1);		
		} else {
			System.out.println("Done.");
		}
	}	
	
	public static void main(String[] args) {		
		Intervals.inline(new VoidInlineTask() {
			@Override
			public void run(Interval subinterval) {
				new RecursiveIntervals(subinterval, 10);
			}
		});
	}
}
