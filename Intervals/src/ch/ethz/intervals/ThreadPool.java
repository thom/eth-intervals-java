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
			final Place place;
			final Semaphore semaphore = new Semaphore(1);
			final WorkerStatistics stats;

			Worker(int id, Place place) {
				super(place.getName() + "-Worker-" + id);
				this.id = id;
				this.place = place;

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
				WorkItem item = place.tasks.take();

				if (item == null) {
					if (numberOfPlaces > 1) {
						item = stealTask();
					}

					if (item == null) {
						if (block) {
							place.idleLock.lock();
							place.idleWorkersExist = true;
							place.idleWorkers.add(this);

							if (WorkerStatistics.ENABLED)
								this.stats.doIdleWorkersAdd();

							place.idleLock.unlock();

							// Blocks until release() is invoked by place
							semaphore.acquireUninterruptibly();
						}

						return false;
					}
				}

				if (Debug.ENABLED)
					Debug.execute(this, item, true);

				item.exec(this);

				return true;
			}

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
		final PlaceStatistics stats;
		final Lock idleLock = new ReentrantLock();
		volatile boolean idleWorkersExist;

		// Guarded by idleLock
		final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

		Place(int id, int[] units) {
			super("Intervals-Place-" + id);
			this.id = id;
			tasks = Config.createQueue(null);
			numberOfWorkers = units.length;
			workers = new Worker[numberOfWorkers];

			for (int i = 0; i < numberOfWorkers; i++) {
				Worker worker = new Worker(units[i], this);
				workers[i] = worker;
				worker.setDaemon(true);
			}

			for (Worker worker : workers)
				worker.start();

			if (PlaceStatistics.ENABLED) {
				stats = new PlaceStatistics(this);
			} else {
				stats = null;
			}
		}

		public String toString() {
			return "(" + getName() + ")";
		}

		public void enqueue(WorkItem item) {
			tasks.put(item);

			// Wake sleeping worker in current place if there's any
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
					if (WorkerStatistics.ENABLED)
						idleWorker.stats.doIdleWorkersRemove();

					idleWorker.semaphore.release();
				}
			}
		}
	}

	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();
	final int numberOfPlaces = Config.places.length;
	final int numberOfWorkers = Config.places.unitsLength;
	final Place[] places = new Place[numberOfPlaces];
	int nextPlace = 0;

	ThreadPool() {
		// Print global statistics and statistics for each worker if worker
		// statistics is enabled
		if (PlaceStatistics.ENABLED) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					// Global place statistics
					// PlaceStatistics.globalPrint();

					// Global worker statistics
					WorkerStatistics.globalPrint();

					// Statistics for each place
					for (Place place : places) {
						// place.stats.print();

						// Statistics for each worker
						for (Worker worker : place.workers) {
							worker.stats.print();
						}
					}
				}
			}));
		}

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
				worker.place.enqueue(item);
			else {
				// Round robin assignment
				Place thisPlace = places[nextPlace];
				nextPlace = (nextPlace + 1) % numberOfPlaces;

				// Wake worker in next place
				Place place = places[nextPlace];
				place.idleLock.lock();
				int l = place.idleWorkers.size();
				if (l > 0) {
					worker = place.idleWorkers.remove(l - 1);
					place.idleWorkersExist = (l != 1);
					place.idleLock.unlock();

					if (WorkerStatistics.ENABLED)
						worker.stats.doIdleWorkersRemove();

					worker.semaphore.release();
				} else {
					place.idleLock.unlock();
				}

				thisPlace.enqueue(item);
			}
		} else {
			places[placeID.id].enqueue(item);
		}
	}
}
