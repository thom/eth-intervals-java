package ch.ethz.intervals;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.PlaceID;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.intervals.ThreadPool.Place.Worker;

class ThreadPool {
	final class Place extends Thread {
		final class Worker extends Thread {
			final int id;
			final Place place;

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

				while (true) {
					doWork();
				}
			}

			/**
			 * Tries to do some work.
			 * 
			 * @return true if work was done, false otherwise
			 */
			boolean doWork() {
				WorkItem item = place.tasks.take();

				if (item == null) {
					if (numberOfPlaces > 1) {
						item = stealTask();
					}

					if (item == null) {
						Thread.yield();
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
		}
	}

	final static ThreadLocal<Worker> currentWorker = new ThreadLocal<Worker>();
	final int numberOfPlaces = Config.places.length;
	final int numberOfWorkers = Config.places.unitsLength;
	final Place[] places = new Place[numberOfPlaces];
	int nextPlace = 0;

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
		PlaceID placeID = item.getPlaceID();

		if (placeID == null) {
			Worker worker = currentWorker();
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
	}
}
