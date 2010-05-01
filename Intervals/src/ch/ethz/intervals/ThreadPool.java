package ch.ethz.intervals;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ThreadPool {

	class KeepAliveThread extends Thread {
		public final Semaphore sem = new Semaphore(1);

		@Override
		public void run() {
			sem.acquireUninterruptibly();
			sem.release();
			return;
		}
	}

	private KeepAliveThread keepAliveThread;

	/**
	 * Starts a keep alive thread that prevents the JVM from exiting. This is
	 * invoked when new work is submitted to the pool from the outside. Always
	 * executed under the {@link #idleLock}.
	 */
	private void startKeepAliveThread() {
		if (keepAliveThread == null) {
			keepAliveThread = new KeepAliveThread();
			keepAliveThread.sem.acquireUninterruptibly();
			keepAliveThread.start();
		}
	}

	/**
	 * Stops the keep alive thread that prevents the JVM from exiting. This is
	 * invoked when all threads become idle. Always executed under the
	 * {@link #idleLock}.
	 */
	private void stopKeepAliveThread() {
		if (keepAliveThread != null) {
			keepAliveThread.sem.release();
			keepAliveThread = null;

			// Print statistics for each worker if worker statistics is enabled
			if (WorkerStatistics.ENABLED) {
				for (Worker worker : workers) {
					worker.stats.print();
				}
			}
		}
	}

	final class Worker extends Thread {
		final int id;
		final Semaphore semaphore = new Semaphore(1);
		final WorkStealingQueue tasks;
		final WorkerStatistics stats;

		Worker(int id) {
			super("Intervals-Worker-" + id);
			this.id = id;
			this.tasks = new LazyDeque(this);

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
			for (int victim = id + 1; victim < numWorkers; victim++)
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

				int length = idleWorkers.size();
				if (length == numWorkers) {
					// All workers asleep.
					stopKeepAliveThread();
				}

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

	final int numWorkers = Runtime.getRuntime().availableProcessors();
	final Worker[] workers = new Worker[numWorkers];
	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();

	final Lock idleLock = new ReentrantLock();

	// guarded by idleLock
	final ArrayList<WorkItem> pendingWorkItems = new ArrayList<WorkItem>();

	// guarded by idleLock
	final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

	volatile boolean idleWorkersExist;

	ThreadPool() {
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
		Worker worker = currentWorker();
		if (worker != null)
			worker.enqueue(item);
		else {
			idleLock.lock();
			startKeepAliveThread();
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
