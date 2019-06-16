package ch.ethz.intervals;

import java.util.Random;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.VoidInlineTask;

public class BarrierTest {
	static class Barrier extends Interval {
		public Barrier(Dependency dep, int id) {
			super(dep, "barrier-" + id);
		}

		@Override
		protected void run() {
			System.out.println(name + " done!");
		}
	}

	static class Task extends Interval {
		private Random random;

		public Task(Dependency dep, int id) {
			super(dep, "task-" + id);
			random = new Random();
		}

		@Override
		protected void run() {
			try {
				Thread.sleep(1000 * random.nextInt(5));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(name + " done!");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Intervals.inline(new VoidInlineTask() {
			public void run(Interval subinterval) {
				Barrier barrier = new Barrier(subinterval, 0);
				for (int i = 0; i < 5; i++) {
					Task task = new Task(subinterval, i);
					Intervals.addHb(task, barrier);
				}
			}
		});
	}

}
