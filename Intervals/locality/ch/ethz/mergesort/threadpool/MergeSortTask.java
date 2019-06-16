package ch.ethz.mergesort.threadpool;

import java.util.concurrent.Callable;

public abstract class MergeSortTask implements Callable<Integer[]> {
	protected final int id;
	protected final String name;
	protected Integer[] array;

	public MergeSortTask(String name, int id) {
		this.id = id;
		this.name = name + id;
	}
}
