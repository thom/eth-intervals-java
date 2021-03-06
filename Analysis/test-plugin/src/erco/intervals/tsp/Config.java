package erco.intervals.tsp;

import java.util.PriorityQueue;

import ch.ethz.intervals.*;
import ch.ethz.intervals.quals.*;

@Creator("Constructor")
@BaseRequirements(instanceMethod="Creator readableBy method")
public class Config {
	final Lock minLock;
	final Lock queueLock;
	final @Creator("queueLock") PriorityQueue<TourElement> queue;	
	final int numNodes;
	final int/*@Creator("this.Constructor")*/[]/*@Creator("this.Constructor")*/[] weights;
	
	@GuardedBy("Constructor") int startNode;
	@GuardedBy("Constructor") int nodesFromEnd;
	
	@GuardedBy("ch.ethz.intervals.guard.RacyGuard#racy") int minTourLength;
	@GuardedBy("minLock") int[] minTour;
	
	Config(int tspSize) {
		minLock = new Lock();
		queueLock = new Lock();
		queue = new PriorityQueue<TourElement>();
		numNodes = tspSize;
		weights = new int[numNodes + 1][numNodes + 1];
		minTour = new int[numNodes + 1];
		minTourLength = Integer.MAX_VALUE;
		nodesFromEnd = 12;
	}
	
	TourElement getTour() {
		return Intervals.inline(new InlineTask<TourElement>() {
			@Override public void init(Interval subinterval) {
				Intervals.addExclusiveLock(subinterval, queueLock);
			}
			@Override public TourElement run(Interval subinterval) {
				return queue.remove();
			}
		});
	}

	public void enqueue(final TourElement newTour) {
		Intervals.inline(new VoidInlineTask() {
			@Override public void init(Interval subinterval) {
				Intervals.addExclusiveLock(subinterval, queueLock);
			}
			@Override public void run(Interval subinterval) {
				queue.add(newTour);
			}
		});
	}
	
	public void setBest(final int curDist, final int[] path) {
		Intervals.inline(new VoidInlineTask() {
			@Override public void init(Interval subinterval) {
				Intervals.addExclusiveLock(subinterval, minLock);
			}
			@Override public void run(Interval subinterval) {
				if(curDist < minTourLength) {
					System.arraycopy(path, 0, minTour, 0, minTour.length);
					minTourLength = curDist;
				}
			}
		});
	}

	public int/*@Creator("hbNow")*/[] getBest() {
		Intervals.inline(new InlineTask<int/*@Creator("hbNow")*/[]>() {
			@Override public void init(Interval subinterval) {
				Intervals.addExclusiveLock(subinterval, minLock);
			}
			@Override public int/*@Creator("hbNow")*/[] run(Interval subinterval) {
			    int[] result = new int /*@Creator("method")*/ [minTour.length];
			    for(int i = 0; i < minTour.length; i++)
			        result[i] = minTour[i];
				return result;
			}
		});
	}

}
