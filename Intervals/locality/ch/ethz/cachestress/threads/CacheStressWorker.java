package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Worker;

abstract class CacheStressWorker extends Thread {
	protected final int id;
	protected final int[] array;
	protected final Worker worker;

	public CacheStressWorker(int id, int[] array) {
		super("cache-stress-worker-" + id);
		this.id = id;
		this.array = array;
		worker = new Worker(id, array);
	}

	public void run() {
		worker.run();
	}
}