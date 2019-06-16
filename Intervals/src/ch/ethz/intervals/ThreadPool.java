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

			Worker(int id, Place place) {
				super(place.getName() + "-Worker-" + id);
				this.id = id;
				this.place = place;
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
							idleLock.lock();
							idleWorkersExist = true;
							idleWorkers.add(this);
							idleLock.unlock();

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
				Place victim = places[victimId];
				WorkItem item = victim.tasks.steal(this);
				return item;
			}
		}

		final int id;
		final WorkStealingQueue tasks;
		final int numberOfWorkers;
		final Worker[] workers;

		Place(int id, int[] units) {
			super("Intervals-Place-" + id);
			this.id = id;
			tasks = Config.createQueue(this);
			numberOfWorkers = units.length;
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
			tasks.put(item);

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
					idleWorker.semaphore.release();
				}
			}
		}
	}

	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();
	final int numberOfPlaces = Config.places.length;
	final int numberOfWorkers = Config.places.unitsLength;
	final Lock idleLock = new ReentrantLock();
	volatile boolean idleWorkersExist = false;
	final Place[] places = new Place[numberOfPlaces];
	int nextPlace = 0;

	// Guarded by idleLock
	final ArrayList<Worker> idleWorkers = new ArrayList<Worker>();

	ThreadPool() {
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
		Worker worker;
		PlaceID placeID = item.getPlaceID();

		if (placeID == null) {
			worker = currentWorker();
			if (worker != null)
				worker.place.enqueue(item);
			else {
				// Round robin assignment
				places[nextPlace].enqueue(item);
				nextPlace = (nextPlace + 1) % numberOfPlaces;
			}
		} else {
			places[placeID.id].enqueue(item);
		}

		idleLock.lock();
		int l = idleWorkers.size();
		if (l > 0) {
			worker = idleWorkers.remove(l - 1);
			idleWorkersExist = (l != 1);
			idleLock.unlock();
			worker.semaphore.release();
		} else {
			idleLock.unlock();
		}
	}
}
