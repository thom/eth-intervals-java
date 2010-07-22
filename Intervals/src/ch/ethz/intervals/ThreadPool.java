package ch.ethz.intervals;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class ThreadPool {
	final class Worker extends Thread {
		final int id;
		final Semaphore semaphore = new Semaphore(1);
		// final WorkStealingQueue tasks;
		final WorkerStatistics stats;

		Worker(int id) {
			super("Intervals-Worker-" + id);
			this.id = id;
			// this.tasks = Config.createQueue(this);

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
					Affinity.set(Config.units.get(id));
				} catch (SetAffinityException e) {
					e.printStackTrace();
				}
			}

			if (Debug.ENABLED) {
				System.err.println(Affinity.getInformation());
			}

			currentWorker.set(this);
			this.semaphore.acquireUninterruptibly(); // cannot fail
			while (true) {
				doWork(true);
			}
		}

		/**
		 * Tries to do some work.
		 * 
		 * @param block
		 *            if true, will block if no work is found, otherwise just
		 *            returns false
		 * @return true if work was done, false otherwise
		 */
		boolean doWork(boolean block) {
			WorkItem item;

			if ((item = pendingWork.take()) == null) {
				if (block) {
					idleLock.lock();
					idleWorkersExist = true;
					idleWorkers.add(this);

					if (WorkerStatistics.ENABLED)
						this.stats.doIdleWorkersAdd();

					idleLock.unlock();

					// blocks until release() is invoked by some other worker
					semaphore.acquireUninterruptibly();
				}

				return false;
			}

			item.exec(this);

			return true;

			// WorkItem item;
			// if ((item = tasks.take()) == null)
			// if ((item = stealTask()) == null)
			// if ((item = findPendingWork(block)) == null)
			// return false;
			//
			// if (Debug.ENABLED)
			// Debug.execute(this, item, true);
			//
			// item.exec(this);
			//
			// return true;
		}

		// private WorkItem stealTask() {
		// WorkItem item;
		// for (int victim = id + 1; victim < numWorkers; victim++)
		// if ((item = stealTaskFrom(victim)) != null)
		// return item;
		// for (int victim = 0; victim < id; victim++)
		// if ((item = stealTaskFrom(victim)) != null)
		// return item;
		// return null;
		// }
		//
		// private WorkItem stealTaskFrom(int victimId) {
		// if (WorkerStatistics.ENABLED)
		// stats.doStealAttempt();
		//
		// Worker victim = workers[victimId];
		// WorkItem item = victim.tasks.steal(this);
		//
		// if (item == null) {
		// if (WorkerStatistics.ENABLED)
		// stats.doStealFailure();
		// } else {
		// if (WorkerStatistics.ENABLED)
		// stats.doStealSuccess();
		// }
		//
		// return item;
		// }
		//
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
		// idleWorker.tasks.put(item);
		// idleWorker.semaphore.release();
		// return;
		// }
		// }
		// if (Debug.ENABLED)
		// Debug.enqeue(this, item);
		// tasks.put(item);
		// }
	}

	final int numWorkers = Config.units.size();
	final Worker[] workers = new Worker[numWorkers];
	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();

	final ReentrantLock idleLock = new ReentrantLock();

	// guarded by idleLock
	// final ArrayList<WorkItem> pendingWorkItems = new ArrayList<WorkItem>();

	// Shared work queue
	final LinkedBlockingWorkStealingDeque pendingWork = new LinkedBlockingWorkStealingDeque(
			null);

	// guarded by idleLock
	final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

	volatile boolean idleWorkersExist;

	ThreadPool() {
		// Print global statistics and statistics for each worker if worker
		// statistics is enabled
		if (WorkerStatistics.ENABLED) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					// Global statistics
					WorkerStatistics.globalPrint();

					// Statistics for each worker
					for (Worker worker : workers) {
						worker.stats.print();
					}
				}
			}));
		}

		for (int i = 0; i < numWorkers; i++) {
			Worker worker = new Worker(i);
			workers[i] = worker;
			worker.setDaemon(true);
		}

		for (Worker worker : workers)
			worker.start();
	}

	Worker currentWorker() {
		return currentWorker.get();
	}

	void submit(WorkItem item) {
		pendingWork.put(item);

		Worker worker;
		idleLock.lock();
		int l = idleWorkers.size();
		if (l > 0) {
			worker = idleWorkers.remove(l - 1);
			idleWorkersExist = (l != 1);
			idleLock.unlock();

			if (WorkerStatistics.ENABLED)
				worker.stats.doIdleWorkersRemove();

			worker.semaphore.release();
		} else {
			idleLock.unlock();
		}
	}
}
