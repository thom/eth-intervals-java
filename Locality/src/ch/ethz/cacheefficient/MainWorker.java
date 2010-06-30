package ch.ethz.cacheefficient;

public abstract class MainWorker extends Thread {
	protected final int id;
	protected MergingWorker mergingWorker;

	public MainWorker(int id) {
		super("main-worker-" + id);
		this.id = id;
	}

	public abstract SortingWorker createSortingWorker(int id, int size);

	public abstract MergingWorker createMergingWorker(int id,
			MergeSortWorker left, MergeSortWorker right);

	public void run() {
		mergingWorker = createMergingWorker(id,
				createSortingWorker(0, Config.ARRAY_SIZE / 2),
				createSortingWorker(1, Config.ARRAY_SIZE / 2));

		mergingWorker.start();

		try {
			mergingWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
