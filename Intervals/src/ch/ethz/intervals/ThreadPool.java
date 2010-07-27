package ch.ethz.intervals;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.intervals.ThreadPool.Place.Worker;

class ThreadPool {
	final class Place extends Thread {
		final class Worker extends Thread {
			final int id;
			final Place owner;
			final Semaphore semaphore = new Semaphore(1);

			// TODO: Move to place (?)
			final WorkerStatistics stats;

			Worker(int id, Place owner) {
				super(owner.getName() + "-Worker-" + id);
				this.id = id;
				this.owner = owner;

				if (WorkerStatistics.ENABLED) {
					stats = new WorkerStatistics(this);
				} else {
					stats = null;
				}
			}

			public String toString() {
				return "(" + getName() + ")";
			}

			@Override
			public void run() {
				if (Config.AFFINITY) {
					try {
						Affinity.set(id);
					} catch (SetAffinityException e) {
						e.printStackTrace();
					}
				}

				if (Debug.ENABLED) {
					System.err.println(Affinity.getInformation());
				}

				currentWorker.set(this);

				// Cannot fail
				this.semaphore.acquireUninterruptibly();

				while (true) {
					doWork(true);
				}
			}

			/**
			 * Tries to do some work.
			 * 
			 * @param block
			 *            if true, will block if no work is found, otherwise
			 *            just returns false
			 * @return true if work was done, false otherwise
			 */
			boolean doWork(boolean block) {
				WorkItem item = owner.tasks.take();

				if (item == null) {
					if (numberOfPlaces > 1) {
						item = stealTask();
					}

					if (item == null) {
						// TODO: Go to sleep instead of calling yield()
						Thread.yield();
						return false;
					}
				}

				if (Debug.ENABLED)
					Debug.execute(this, item, true);

				item.exec(this);

				return true;
			}

			// private WorkItem findPendingWork(boolean block) {
			// idleLock.lock();
			//
			// if (WorkerStatistics.ENABLED)
			// this.stats.doPendingWorkItemsRemoveAttempt();
			//
			// int l = pendingWorkItems.size();
			// if (l != 0) {
			// WorkItem item = pendingWorkItems.remove(l - 1);
			//
			// if (WorkerStatistics.ENABLED)
			// this.stats.doPendingWorkItemsRemove();
			//
			// idleLock.unlock();
			// return item;
			// } else if (block) {
			// idleWorkersExist = true;
			// idleWorkers.add(this);
			//
			// if (WorkerStatistics.ENABLED)
			// this.stats.doIdleWorkersAdd();
			//
			// int length = idleWorkers.size();
			// if (length == numWorkers) {
			// // All workers asleep.
			// // stopKeepAliveThread();
			// }
			//
			// idleLock.unlock();
			//
			// // blocks until release() is invoked by some other worker
			// semaphore.acquireUninterruptibly();
			//
			// return null;
			// }
			//
			// idleLock.unlock();
			// return null;
			// }

			private WorkItem stealTask() {
				WorkItem item;

				for (int victim = id + 1; victim < numberOfPlaces; victim++)
					if ((item = stealTaskFrom(victim)) != null)
						return item;
				for (int victim = 0; victim < numberOfPlaces; victim++)
					if ((item = stealTaskFrom(victim)) != null)
						return item;

				return null;
			}

			private WorkItem stealTaskFrom(int victimId) {
				if (WorkerStatistics.ENABLED)
					stats.doStealAttempt();

				Place victim = places[victimId];
				WorkItem item = victim.tasks.steal(this);

				if (WorkerStatistics.ENABLED) {
					if (item == null)
						stats.doStealFailure();
					else
						stats.doStealSuccess();
				}

				return item;
			}
		}

		final int id;
		final WorkStealingQueue tasks;
		final int numberOfWorkers;
		final Worker[] workers;
		final Lock idleLock = new ReentrantLock();
		volatile boolean idleWorkersExist;

		// Guarded by idleLock
		final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

		Place(int id, int[] units) {
			super("Intervals-Place-" + id);
			this.id = id;

			// TODO: Set place as owner
			this.tasks = Config.createQueue(null);

			this.numberOfWorkers = units.length;
			workers = new Worker[numberOfWorkers];

			for (int i = 0; i < numberOfWorkers; i++) {
				Worker worker = new Worker(units[i], this);
				workers[i] = worker;
				worker.setDaemon(true);
			}

			for (Worker worker : workers)
				worker.start();
		}

		public String toString() {
			return "(" + getName() + ")";
		}

		public void enqueue(WorkItem item) {
			// TODO: Wake sleeping worker if there's any
			tasks.put(item);
		}

		// void enqueue(WorkItem item) {
		// if (idleWorkersExist) {
		// Worker idleWorker = null;
		// idleLock.lock();
		// try {
		// int l = idleWorkers.size();
		// if (l != 0) {
		// idleWorker = idleWorkers.remove(l - 1);
		// idleWorkersExist = (l != 1);
		// }
		// } finally {
		// idleLock.unlock();
		// }
		//
		// if (idleWorker != null) {
		// if (Debug.ENABLED)
		// Debug.awakenIdle(this, item, idleWorker);
		//
		// if (WorkerStatistics.ENABLED)
		// idleWorker.stats.doIdleWorkersRemove();
		//
		// // idleWorker.tasks.put(item);
		// idleWorker.semaphore.release();
		// return;
		// }
		// }
		// if (Debug.ENABLED)
		// Debug.enqeue(this, item);
		// tasks.put(item);
		// }
	}

	final int numWorkers = Config.places.unitsLength;
	final Worker[] workers = new Worker[numWorkers];
	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();
	final int numberOfPlaces = Config.places.length;
	final Place[] places = new Place[numberOfPlaces];
	int nextPlace = 0;

	ThreadPool() {
		// TODO: Fix statistics
		// Print global statistics and statistics for each worker if worker
		// statistics is enabled
		// if (WorkerStatistics.ENABLED) {
		// Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		// public void run() {
		// // Global statistics
		// WorkerStatistics.globalPrint();
		//
		// // Statistics for each worker
		// for (Worker worker : workers) {
		// worker.stats.print();
		// }
		// }
		// }));
		// }

		for (int i = 0; i < numberOfPlaces; i++) {
			Place place = new Place(i, Config.places.get(i));
			places[i] = place;
			place.setDaemon(true);
		}

		for (Place place : places)
			place.start();
	}

	Worker currentWorker() {
		return currentWorker.get();
	}

	void submit(WorkItem item) {
		PlaceID placeID = item.getPlaceID();

		if (placeID == null) {
			Worker worker = currentWorker();
			if (worker != null)
				worker.owner.enqueue(item);
			else {
				// Round robin assignment
				places[nextPlace].enqueue(item);
				nextPlace = (nextPlace + 1) % numberOfPlaces;

				// TODO: Wake worker in next place
				// Worker worker = currentWorker();
				// if (worker != null)
				// worker.enqueue(item);
				// else {
				// idleLock.lock();
				// // startKeepAliveThread();
				// int l = idleWorkers.size();
				// if (l == 0) {
				// // No one waiting to take this job.
				// // Put it on the list of pending items, and someone will get
				// to
				// // it rather than becoming idle.
				// pendingWorkItems.add(item);
				//
				// if (Debug.ENABLED)
				// Debug.enqeue(null, item);
				//
				// if (WorkerStatistics.ENABLED)
				// WorkerStatistics.doPendingWorkItemsAdd();
				//
				// idleLock.unlock();
				// } else {
				// // There is an idle worker. Remove it, assign it this job,
				// and
				// // wake it.
				// worker = idleWorkers.remove(l - 1);
				// idleWorkersExist = (l != 1);
				// idleLock.unlock();
				//
				// if (WorkerStatistics.ENABLED)
				// worker.stats.doIdleWorkersRemove();
				//
				// if (Debug.ENABLED)
				// Debug.awakenIdle(null, item, worker);
				//
				// // worker.tasks.put(item);
				// worker.semaphore.release();
				// }
				// }
			}
		} else {
			places[placeID.id].enqueue(item);
		}
	}
}
