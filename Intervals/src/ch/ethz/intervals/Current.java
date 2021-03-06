package ch.ethz.intervals;

import static ch.ethz.intervals.Intervals.SAFETY_CHECKS;

class Current {
	
	private static ThreadLocal<Current> local = new ThreadLocal<Current>() {

		@Override
		protected Current initialValue() {
			return new Current();
		}
		
	};
	
	static Current get() {
		return local.get();
	}
	
	static Current push(Interval inter) {
		Current c = get();
		Current n = new Current(c, inter);
		local.set(n);
		return n;
	}
	
	final Current prev;           /** Previous Current on stack. */
	final Interval inter;         /** Smallest containing interval. {@code null} if root. */
	Point mr;                  	  /** Most recent point on inter.line that occurred. Not always inter.start! May be {@code null} if root. */
	private Interval unscheduled; /** Linked list of unscheduled intervals. */
	
	private Current() {
		this.prev = null;
		this.inter = null;
		this.mr = null;
		this.unscheduled = null;
	}

	Current(Current prev, Interval inter) {
		assert inter != null && inter.start.didOccur();
		this.prev = prev;
		this.inter = inter;
		this.mr = inter.start;
	}
	
	void updateMostRecent(Point mr) {
		assert mr.didOccur();
		this.mr = mr;
	}
	
	void addUnscheduled(Interval interval) {
		interval.nextUnscheduled = unscheduled;
		unscheduled = interval;
	}
	
	boolean isUnscheduled(Point pnt) {
		Interval interval = pnt.racyInterval();
		if(interval == null)
			return false;
		return interval.isUnscheduled(this);
	}

	void schedule(Interval interval) {
		Interval p = unscheduled;
		
		if(p == interval) {
			unscheduled = interval.nextUnscheduled;
		} else {
			while(p != null && p.nextUnscheduled != interval)
				p = p.nextUnscheduled;
			
			if(p == null)
				throw new IntervalException.AlreadyScheduled(interval);
			
			p.nextUnscheduled = interval.nextUnscheduled;
		}
		
		interval.nextUnscheduled = null;
		scheduleUnchecked(interval);
	}

	void schedule() {
		Interval p = unscheduled;
		while(p != null) {
			scheduleUnchecked(p);
			
			Interval n = p.nextUnscheduled;
			p.nextUnscheduled = null;
			p = n;
		}
		unscheduled = null;
	}

	private void scheduleUnchecked(Interval p) {
		assert p.isUnscheduled(this);
		
		if(Debug.ENABLED)
			Debug.schedule(p, inter);
		ExecutionLog.logScheduleInterval(p);
		
		p.clearUnscheduled();
		p.start.arrive(1);
	}

	void pop() {
		assert unscheduled == null;
		local.set(prev);
	}

	void checkCanAddChild(Interval parent) {
		if(SAFETY_CHECKS) {
			if(isUnscheduled(parent.start))
				return;
			if(inter == null)
				throw new NotInRootIntervalException();
			if(inter.end.isBoundedByOrEqualTo(parent.end))
				return;
			if(inter.end.hbeq(parent.start))
				return;
			throw new EdgeNeededException(inter.end, parent.start);
		}
	}

	void checkCanAddDep(Point to) {		
		if(SAFETY_CHECKS) {
			if(isUnscheduled(to))
				return;
			if(inter != null && inter.end.hbeq(to))
				return;
			throw new EdgeNeededException((inter != null ? inter.end : null), to);
		}		
	}
	
	void checkEdgeEndPointsProperlyBound(Point from, Point to) {
		// An edge p->q is only permitted if q is bound by
		// p's parent.  In other words, edges are permitted from
		// high-level nodes down the tree, but not the other direction.
		
		Point parentEnd;
		if(from.isStartPoint())
			parentEnd = from.bound.bound;
		else
			parentEnd = from.bound;
			
		if(parentEnd == null || to.isBoundedBy(parentEnd))
			return;
		
		throw new MustBeBoundedByException(from.bound, to);
	}

	void checkCanAddHb(Point from, Point to) {
		if(SAFETY_CHECKS) {
			checkCanAddDep(to);
			checkEdgeEndPointsProperlyBound(from, to);
			checkCycle(from, to);
		}
	}

	void checkCycle(Point from, Point to) {
		if(SAFETY_CHECKS) {
			if(from != null && to.hb(from))
				throw new CycleException(from, to);
		}
	}

	public Point start() {
		if(inter != null)
			return inter.start;
		return null;
	}

}
