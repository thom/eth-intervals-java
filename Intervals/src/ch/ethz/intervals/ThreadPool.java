package ch.ethz.intervals;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.SetAffinityException;

class ThreadPool {
	final class Worker extends Thread {
		final int id;
		final Semaphore semaphore = new Semaphore(1);
		final WorkStealingQueue tasks;

		Worker(int id) {
			super("Intervals-Worker-" + id);
			this.id = id;
			this.tasks = Config.createQueue();
		}

		public String toString() {
			return "(" + getName() + ")";
		}

		@Override
		public void run() {
			if (Config.AFFINITY) {
				try {
					Affinity.set(Config.places.getUnit(id));
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
			if ((item = tasks.take()) == null)
				if ((item = stealTask()) == null)
					if ((item = findPendingWork(block)) == null)
						return false;

			if (Debug.ENABLED)
				Debug.execute(this, item, true);

			item.exec(this);

			return true;
		}

		private WorkItem stealTask() {
			WorkItem item;
			for (int victim = id + 1; victim < numberOfWorkers; victim++)
				if ((item = stealTaskFrom(victim)) != null)
					return item;
			for (int victim = 0; victim < id; victim++)
				if ((item = stealTaskFrom(victim)) != null)
					return item;
			return null;
		}

		private WorkItem stealTaskFrom(int victimId) {
			Worker victim = workers[victimId];
			WorkItem item = victim.tasks.steal(this);
			return item;
		}

		private WorkItem findPendingWork(boolean block) {
			idleLock.lock();
			int l = pendingWorkItems.size();
			if (l != 0) {
				WorkItem item = pendingWorkItems.remove(l - 1);
				idleLock.unlock();
				return item;
			} else if (block) {
				idleWorkersExist = true;
				idleWorkers.add(this);
				idleLock.unlock();

				// blocks until release() is invoked by some other worker
				semaphore.acquireUninterruptibly();

				return null;
			}

			idleLock.unlock();
			return null;
		}

		void enqueue(WorkItem item) {
			if (idleWorkersExist) {
				Worker idleWorker = null;
				idleLock.lock();
				try {
					int l = idleWorkers.size();
					if (l != 0) {
						idleWorker = idleWorkers.remove(l - 1);
						idleWorkersExist = (l != 1);
					}
				} finally {
					idleLock.unlock();
				}

				if (idleWorker != null) {
					if (Debug.ENABLED)
						Debug.awakenIdle(this, item, idleWorker);

					idleWorker.tasks.put(item);
					idleWorker.semaphore.release();
					return;
				}
			}
			if (Debug.ENABLED)
				Debug.enqeue(this, item);
			tasks.put(item);
		}

	}

	final int numberOfWorkers = Config.places.unitsLength;
	final Worker[] workers = new Worker[numberOfWorkers];
	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();

	final Lock idleLock = new ReentrantLock();

	// guarded by idleLock
	final ArrayList<WorkItem> pendingWorkItems = new ArrayList<WorkItem>();

	// guarded by idleLock
	final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

	volatile boolean idleWorkersExist;

	ThreadPool() {
		for (int i = 0; i < numberOfWorkers; i++) {
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
		Worker worker = currentWorker();
		if (worker != null)
			worker.enqueue(item);
		else {
			idleLock.lock();
			// startKeepAliveThread();
			int l = idleWorkers.size();
			if (l == 0) {
				// No one waiting to take this job.
				// Put it on the list of pending items, and someone will get to
				// it rather than becoming idle.
				pendingWorkItems.add(item);

				if (Debug.ENABLED)
					Debug.enqeue(null, item);

				idleLock.unlock();
			} else {
				// There is an idle worker. Remove it, assign it this job, and
				// wake it.
				worker = idleWorkers.remove(l - 1);
				idleWorkersExist = (l != 1);
				idleLock.unlock();

				if (Debug.ENABLED)
					Debug.awakenIdle(null, item, worker);

				worker.tasks.put(item);
				worker.semaphore.release();
			}
		}
	}
}
