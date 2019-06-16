package ch.ethz.cachestress.threads;

import ch.ethz.cachestress.Task;

abstract class CacheStressTask extends Thread {
	protected final int id;
	protected final int[] array;
	protected final Task task;

	public CacheStressTask(int id, int[] array) {
		super("cache-stress-task-" + id);
		this.id = id;
		this.array = array;
		task = new Task(id, array);
	}

	public void run() {
		task.run();
	}
}