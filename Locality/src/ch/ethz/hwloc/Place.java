package ch.ethz.hwloc;

/**
 * Place
 * 
 * @author Thomas Weibel
 */
public abstract class Place {
	private int place = -1;

	public Place() {
		place = -1;
	}

	public Place(int place) {
		set(place);
	}

	public void set(int place) {
		// TODO: set
		this.place = place;
	}

	public int get() {
		return place;
	}

	public String toString() {
		String result = String.format("Thread ID: %d", getThreadId());
		result += String.format("\nPlace: %d", place);
		for (int core : placeToCores(place)) {
			result += String.format("\n  * Core %d", core);
		}

		try {
			boolean[] affinities = getAffinity();
			result += "\nLocality:";
			for (int i = 0; i < affinities.length; i++) {
				result += String.format("\n  * Core %d (Place %d)", i,
						coreToPlace(i));
			}
		} catch (GetAffinityException e) {
			e.printStackTrace();
		}

		return result;
	}

	public abstract int coreToPlace(int core);

	public abstract int[] placeToCores(int place);

	private native void setAffinity(int[] physicalUnits);

	private native boolean[] getAffinity() throws GetAffinityException;

	private native int getThreadId();

	static {
		System.loadLibrary("Place");
	}
}