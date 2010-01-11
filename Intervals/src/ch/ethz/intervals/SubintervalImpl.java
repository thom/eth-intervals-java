package ch.ethz.intervals;

final class SubintervalImpl<R> extends Interval {

	SubintervalTask<R> task;
	R result;
	
	SubintervalImpl(Interval superInterval, Line line, Point nextEpoch, SubintervalTask<R> task) {
		super(superInterval, line, 0, 2, nextEpoch);
		assert end.maskExceptions();
		this.task = task;		
	}
	
	@Override
	public String toString() {
		return task.toString();
	}

	@Override
	protected void run() {
		result = task.run(this);
	}
	
}
