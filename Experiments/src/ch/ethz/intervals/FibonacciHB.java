package ch.ethz.intervals;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.InlineTask;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;

public class FibonacciHB extends Interval {
	public int number, depth;

	FibonacciHB(Dependency dep, String name, int depth, int number) {
		super(dep, name);
		this.depth = depth;
		this.number = number;
	}

	public String toString() {
		return String.format("Fib(%d), depth=%d, %s", number, depth, super
				.toString());
	}

	public void out() {
		System.out.println(toString());
	}

	class SetResult extends Interval {
		public int number;

		public SetResult(Dependency dep, int number) {
			super(dep);
			this.number = number;
		}

		@Override
		protected void run() {
			// Nothing to do!
		}
	}

	@Override
	protected void run() {
		foo(parent, "root", depth, number);
	}

	private Interval foo(Dependency dep, String name, int depth, int number) {
		Interval result;

		int n = number;
		this.out();

		if (n < 2) {
			result = new SetResult(parent, n);
		} else {
			final Interval nMinusOne = new FibonacciHB(parent, "n-1",
					depth + 1, n - 1);
			final Interval nMinusTwo = new FibonacciHB(parent, "n-2",
					depth + 1, n - 2);
			result = new SetResult(parent, ((FibonacciHB) nMinusOne).number
					+ ((FibonacciHB) nMinusTwo).number);
			Intervals.addHb(nMinusOne, result);
			Intervals.addHb(nMinusTwo, result);
		}
		//Intervals.schedule();
		return result;
	}

	public static void main(String[] args) {
		final int n = 5;
		FibonacciHB fib = Intervals.inline(new InlineTask<FibonacciHB>() {
			@Override
			public FibonacciHB run(Interval subinterval) {
				return new FibonacciHB(subinterval, "root", 0, n);
			}
		});
		System.out.println(fib.number);
	}

}
