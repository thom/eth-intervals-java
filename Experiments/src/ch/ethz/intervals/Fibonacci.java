package ch.ethz.intervals;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.InlineTask;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;

public class Fibonacci extends Interval {
	static final int THRESHOLD = 13;
	int number;

	public Fibonacci(Dependency dep, int number) {
		super(dep);
		this.number = number;
	}

	@Override
	protected void run() {
		final int n = number;

		if (n <= THRESHOLD) {
			number = sequentialFibonacci(n);
		} else {
			Fibonacci fib1 = Intervals.inline(new InlineTask<Fibonacci>() {
				public Fibonacci run(Interval subinterval) {
					return new Fibonacci(subinterval, n - 1);
				}
			});

			Fibonacci fib2 = Intervals.inline(new InlineTask<Fibonacci>() {
				public Fibonacci run(Interval subinterval) {
					return new Fibonacci(subinterval, n - 2);
				}
			});

			number = fib1.number + fib2.number;
		}
	}

	int sequentialFibonacci(int n) {
		if (n <= 1) {
			return n;
		} else {
			return sequentialFibonacci(n - 1) + sequentialFibonacci(n - 2);
		}
	}

	public static void main(final String[] args) {
		final int n = Integer.parseInt(args[0]);

		Fibonacci fib = Intervals.inline(new InlineTask<Fibonacci>() {
			public Fibonacci run(Interval subinterval) {
				return new Fibonacci(subinterval, n);
			}
		});

		System.out.println(fib.number);

		assert fib.number == fib.sequentialFibonacci(n);
	}
}