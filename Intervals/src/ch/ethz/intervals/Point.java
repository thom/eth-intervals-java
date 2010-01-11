package ch.ethz.intervals;

import static ch.ethz.intervals.EdgeList.NONDETERMINISTIC;
import static ch.ethz.intervals.EdgeList.SPECULATIVE;
import static ch.ethz.intervals.EdgeList.WAITING;
import static ch.ethz.intervals.EdgeList.speculative;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import ch.ethz.intervals.ThreadPool.Worker;

final public class Point {
	
	public static final int END_EPOCH = Integer.MAX_VALUE;
	
	public static final int OCCURRED = -1;	
	
	public static final int initialWaitCount = Integer.MAX_VALUE;
	
	private static AtomicIntegerFieldUpdater<Point> waitCountUpdater =
		AtomicIntegerFieldUpdater.newUpdater(Point.class, "waitCount");

	static final int NO_POINT_FLAGS = 0;

	/** If set, then we must acquire locks for {@link #line} before occurring. */
	static final int FLAG_ACQUIRE_LOCKS = 2;

	final Line line;
	
	final Point end;
	/*private*/ volatile Point nextEpoch;
	
	private EdgeList outEdges;                /** Linked list of outgoing edges from this point. */
	private volatile int waitCount;           /** Number of preceding points that have not arrived.  
	                                              Set to {@link #OCCURRED} when this has occurred. 
	                                              Modified only through {@link #waitCountUpdater}. */
	private Set<Throwable> pendingExceptions; /** Exception(s) that occurred while executing the task or in some preceding point. */
	private int flags;                  	  /** Flags (see integer constants like {@link #FLAG_ACQUIRE_LOCKS}) */
	private Interval workItem;            	  /** Work to schedule when we occur (if any) */

	Point(Line line, Point end, Point nextEpoch, int flags, int waitCount, Interval workItem) {
		this.line = line;
		this.nextEpoch = nextEpoch;
		this.end = end;
		this.flags = flags;
		this.waitCount = waitCount;
		this.workItem = workItem;
	}
	
	boolean maskExceptions() {
		// The end of any subinterval masks exceptions
		return (end == null && nextEpoch != null);
	}
	
	private <R> SubintervalImpl<R> insertSubintervalBefore(Interval current, SubintervalTask<R> task) {
		// See insertSubintervalAfter() for details on the points we are
		// creating.
		assert !didOccur() && line == current.line;
		addWaitCount();
		Point subEnd = new Point(line, null, this, NO_POINT_FLAGS, 2, null);
		Point subStart = new Point(line, subEnd, subEnd, NO_POINT_FLAGS, 0, null);
		return new SubintervalImpl<R>(current, subStart, subEnd, task);
	}
	
	<R> SubintervalImpl<R> insertSubintervalAfter(Interval current, SubintervalTask<R> task) {
		// In the very beginning there are 2 points and we are point 0:
		//    0----END
		// When we are done there will be 4:
		//    0----1----2----END
		//    ^----^----^----^
		//     pre  sub  post
		// Where pre is the current span of time, sub is the subinterval,
		// and post is the span of time after the subinterval.
		assert didOccur();
		SubintervalImpl<R> subinter = nextEpoch.insertSubintervalBefore(current, task);
		nextEpoch = subinter.start;
		return subinter;
	}
	
	synchronized EdgeList outEdgesSync() {
		return outEdges;
	}

	boolean didOccur() {
		return waitCount == OCCURRED;
	}
	
	Point nextEpochOrBound() {
		Point nextEpoch = this.nextEpoch;
		if(nextEpoch != null) return nextEpoch;
		return line.bound;
	}
	
	boolean isRootEnd() {
		return nextEpochOrBound() == null;
	}
	
	@Override
	public String toString() {
		return "Point("+System.identityHashCode(this)+"/"+line+")";
	}
	
	public Point mutualBound(Point j) {
		
		// NOTE:
		//
		// No synchronization is required here.  True?  		
		
		Point i = this;
		
		while(i != j) {
			if(i.line == j.line) {
				for(Point e = i.nextEpoch; e != null; e = e.nextEpoch)
					if(e == j) return j;
				for(Point e = j.nextEpoch; e != null; e = e.nextEpoch)
					if(e == i) return i;
				throw new RuntimeException("Two points on same line but neither is first?");
			} else if (i.line.depth > j.line.depth) {
				i = i.line.bound;
			} else if (i.line.depth < j.line.depth) {
				j = j.line.bound;
			} else {
				i = i.line.bound;
				j = j.line.bound;
			}
		}
		
		return i;
	}
	
	/** True if {@code p} bounds {@code this} or one of its bounds */
	public boolean isBoundedBy(Point p) {
		return mutualBound(p) == p; // TODO Make more efficient
	}

	/** Returns true if {@code this} <i>happens before</i> {@code p} */
	public boolean hb(final Point p) {
		return hb(p, NONDETERMINISTIC | SPECULATIVE);
	}
	
	/** Returns true if {@code this == p} or {@code this} <i>happens before</i> {@code p} */
	public boolean hbeq(final Point p) {
		return (this == p) || hb(p);
	}
	
	boolean hbeq(Point p, int skipFlags) {
		return (this == p) || hb(p, skipFlags);
	}
	
	/** true if {@code this} -> {@code p}.
	 * @param p another point
	 * @param skipFlags Skips edges which have any of the flags in here. */
	boolean hb(final Point p, final int skipFlags) {
		assert p != null;
		
		// Some simple checks:
		if(p == this)
			return false;
		if(isBoundedBy(p))
			return true;
		if(p.isBoundedBy(this))
			return false;
		
		// Perform a BFS to find p:		
		final Queue<Point> queue = new LinkedList<Point>();
		final Set<Point> visited = new HashSet<Point>(64);
		queue.add(this);
		
		class Helper {
			boolean foundIt = false;
			
			/** Add {@code q} to queue if not already visited, returning
			 *  {@code true} if {@code q} is the node we are looking for. */
			boolean tryEnqueue(Point q) {
				if(q != null) {				
					if(q == p) {
						foundIt = true;
						return true; // found it
					}
	
					// TODO: We can optimize here.  For example,
					// if p is bounded by q, then p->q, so q cannot hb p.
					//
					// (If the user attempted to add such an edge, a cycle
					//  exception would result.)
					if(visited.add(q))
						queue.add(q);
				}
				return false;
			}
		}
		final Helper h = new Helper();
		
		do {
			Point q = queue.remove();
			
			if(h.tryEnqueue(q.nextEpochOrBound()))				
				return true;
			
			// Do we need to use synchronized accessor here?
			// Would it be good enough to use a volatile field for outEdges?
			new EdgeList.InterruptibleIterator(q.outEdgesSync()) {
				public boolean forEach(Point toPoint, int flags) {
					if((flags & skipFlags) == 0) {
						return h.tryEnqueue(toPoint);
					}
					return false;
				}
			};
		} while(!queue.isEmpty() && !h.foundIt);
		
		return h.foundIt;
	}

	/** When a preceding point (or something else we were waiting for)
	 *  occurs, this method is invoked. 
	 *  @param cnt the number of preceding things that occurred,
	 *  should always be positive and non-zero */
	protected final void arrive(int cnt) {
		int newCount = waitCountUpdater.addAndGet(this, -cnt);
		
		if(Debug.ENABLED) {
			Debug.arrive(this, cnt, newCount);
		}		
		
		assert newCount >= 0;
		if(newCount == 0 && cnt != 0)
			if((flags & FLAG_ACQUIRE_LOCKS) != 0)
				acquirePendingLocks();
			else
				occur();
	}
	
	/** Invoked when the wait count is zero.  Attempts to
	 *  acquire all pending locks. */
	protected final void acquirePendingLocks() {
		flags &= ~FLAG_ACQUIRE_LOCKS;
		waitCount = 1; // Wait for us to finish iterating through the list of locks.		
		this.workItem.acquirePendingLocks();		
		arrive(1);
	}
	
	/** Invoked when the wait count is zero and all pending locks
	 *  are acquired. Each point occurs precisely once. */
	final void occur() {
		assert !isRootEnd();
		assert waitCount == 0;
		assert line.isScheduled();
		
		// Save copies of our outgoing edges at the time we occurred:
		//      They may be modified further while we are notifying successors.
		final Point nextEpoch;
		final EdgeList outEdges;
		synchronized(this) {
			outEdges = this.outEdges;
			nextEpoch = this.nextEpoch;
			this.waitCount = OCCURRED;
			notifyAll(); // in case anyone is joining us
		}
		
		ExecutionLog.logArrive(this);
		
		if(Debug.ENABLED)
			Debug.occur(this, outEdges);
		
		// Notify our successors:
		new EdgeList.Iterator(outEdges) {
			public void doForEach(Point toPoint, int flags) {
				if(EdgeList.waiting(flags))
					notifySuccessor(toPoint, true);
			}
		};
		if(nextEpoch != null)
			notifySuccessor(nextEpoch, true);
		else
			notifySuccessor(line.bound, true);
		
		if(workItem != null) {
			workItem.fork((pendingExceptions != null));
			workItem = null;
		}		
	}
	
	/** Takes the appropriate action to notify a successor {@code pnt}
	 *  that {@code this} has occurred.  Propagates exceptions and 
	 *  optionally invokes {@link #arrive(int)}. */
	private void notifySuccessor(Point pnt, boolean arrive) {
		if(!maskExceptions() && pendingExceptions != null)
			pnt.addPendingExceptions(pendingExceptions);
		if(arrive)
			pnt.arrive(1);
	}

	/** Adds to the wait count.  Only safe when the caller is
	 *  one of the people we are waiting for.  
	 *  <b>Does not acquire a lock on this.</b> */
	protected void addWaitCount() {
		int newCount = waitCountUpdater.incrementAndGet(this);
		assert newCount > 1;
		if(Debug.ENABLED)
			Debug.addWaitCount(this, newCount);		
	}

	protected void subWaitCountUnsync(int cnt) {
		int newCount = waitCount - cnt;
		waitCount = newCount;
		assert newCount > 0;
	}
	
	/** Adds to the wait count but only if the point has
	 *  not yet occurred.  Always safe.
	 *  @return true if the add succeeded. */
	synchronized boolean tryAddWaitCount() {
		if(didOccur())
			return false;
		waitCount++;
		return true;
	}
	
	/**
	 * Waits until this point has occurred.  If this is a helper thread,
	 * tries to do useful work in the meantime!
	 */
	void join() {
		Worker worker = Intervals.POOL.currentWorker();
	
		if(worker == null) {
			synchronized(this) {
				while(!didOccur())
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e); // Should never happen
					}
			}
		} else {
			while(true) {
				int wc;
				synchronized(this) {
					wc = waitCount;
				}
				if(wc == OCCURRED)
					return;
				if(!worker.doWork(false))
					Thread.yield();				
			}
		}
	}

	/** Checks if a pending throwable is stored and throws a
	 *  {@link RethrownException} if so. */
	void checkAndRethrowPendingException() {
		if(pendingExceptions != null)
			throw new RethrownException(pendingExceptions);				
	}
	
	/** Adds {@code thr} to {@link #pendingExceptions} */
	synchronized void addPendingException(Throwable thr) {
		assert !didOccur() : "Cannot add a pending exception after pnt occurs!";
		if(isRootEnd()) {
			thr.printStackTrace();
		} else {
			// Using a PSet<> would really be better here:
			if(pendingExceptions == null)
				pendingExceptions = Collections.singleton(thr);
			else if(pendingExceptions.size() == 1) {
				pendingExceptions = new HashSet<Throwable>(pendingExceptions);
				pendingExceptions.add(thr);
			} else {
				pendingExceptions.add(thr);
			}
		}
	}


	/** Invoked when a child interval has thrown the exceptions {@code thr}. */
	synchronized void addPendingExceptions(Set<Throwable> thr) {
		for(Throwable t : thr)
			addPendingException(t);
	}

	/** Simply adds an outgoing edge, without acquiring locks or performing any
	 *  further checks. */
	private void primAddOutEdge(Point targetPnt, int flags) {
		outEdges = EdgeList.add(outEdges, targetPnt, flags);
	}
	
	void addSpeculativeEdge(Point targetPnt, int flags) {
		synchronized(this) {
			primAddOutEdge(targetPnt, flags | EdgeList.SPECULATIVE);
		}
	}
	
	/**
	 * Optimized routine for the case where 'this' is known to have
	 * already occurred and not to have had any exceptions.
	 */
	void addEdgeAfterOccurredWithoutException(Point targetPnt, int edgeFlags) {
		synchronized(this) {
			assert didOccur();
			
			// In some cases, pendingExceptions may be non-null if this was a 
			// subinterval which rethrew the exceptions and had them caught.
			assert maskExceptions() || pendingExceptions == null;
			primAddOutEdge(targetPnt, edgeFlags);
		}
	}

	/**
	 * Adds an edge from {@code this} to {@code toImpl}, doing no safety
	 * checking, and adjusts the wait count of {@code toImpl}.
	 *  
	 * Returns the number of wait counts added to {@code toImpl}.
	 */
	void addEdgeAndAdjust(Point toImpl, int flags) {		
		assert !speculative(flags) : "addEdgeAndAdjust should not be used for spec. edges!";
		
		// Note: we must increment the wait count before we release
		// the lock on this, because otherwise toImpl could arrive and
		// then decrement the wait count before we get a chance to increment
		// it.  Therefore, it's important that we do not have to acquire a lock
		// on toImpl, because otherwise deadlock could result.
		synchronized(this) {			
			if(!didOccur()) {
				primAddOutEdge(toImpl, flags | EdgeList.WAITING); 
				toImpl.addWaitCount();
				return;
			} else {
				primAddOutEdge(toImpl, flags);
			}
		}
		
		// If we already occurred, then we may still have to push the pending
		// exceptions, but we do not need to invoke arrive():
		assert didOccur();
		notifySuccessor(toImpl, false);
	}
	
	/** Removes an edge to {@code toImpl}, returning true 
	 *  if this point has occurred. */
	void unAddEdge(Point toImpl) {
		synchronized(this) {
			EdgeList.remove(outEdges, toImpl);
		}
	}

	void confirmEdgeAndAdjust(Point toImpl, int flags) {
		
		// Careful
		//
		// The edge to toImpl was speculative.  We are now going
		// to convert it into an ordinary edge.  If we are doing this
		// conversion before we occurred, then we will have to set the
		// WAITING flag on it and add to its wait count.
		//
		// Otherwise, we just leave its ref count alone, but we may have
		// to propagate pendingExceptions to it.
		
		synchronized(this) {
			if(!didOccur()) {
				toImpl.addWaitCount();
				EdgeList.removeSpeculativeFlagAndAdd(outEdges, toImpl, WAITING);
				return;
			} else {
				EdgeList.removeSpeculativeFlagAndAdd(outEdges, toImpl, 0);
			}			
		} 
		
		// If we get here, then we occurred either before the speculative
		// edge was added, or while it was being confirmed.  In that case, 
		// we have to notify it of any pending exceptions, but we do not
		// need to invoke accept():
		assert didOccur();	
		notifySuccessor(toImpl, false);
	}

	/** Returns an array {@code bounds} where {@code bounds[0]} == the bound at depth 0,
	 *  {@code bounds[depth] == this} */
	Point[] bounds() {
		Point[] bounds = new Point[line.depth+1];
		Point b = this;
		for(int i = line.depth; i >= 0; i--) {
			bounds[i] = b;
			b = b.line.bound;
		}
		return bounds;
	}

}
