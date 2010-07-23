package ch.ethz.cachestress.threadpool;

import ch.ethz.cachestress.Task;

public class CacheStressTask implements Runnable {
	protected final int id;
	protected final String name;
	protected final int[] array;
	protected final Task task;

	public CacheStressTask(int id, int[] array) {
		this.id = id;
		this.name = "cache-stress-task-" + id;
		this.array = array;
		task = new Task(id, array);
	}

	public void run() {
		task.run();
	}
}