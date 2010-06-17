package ch.ethz.hwloc;

/**
 * Abstract place
 * 
 * @author Thomas Weibel
 */
public abstract class Place {
	private int place = -1;

	public Place() {
		place = -1;
	}

	public void set(int place) throws SetAffinityException {
		this.place = place;
	}

	public int get() {
		return place;
	}

	public boolean isSet() {
		return place != -1;
	}

	public String toString() {
		String result = String.format("Thread ID: %d", getThreadId());

		if (isSet()) {
			result += String.format("\nPlace: %d", place);
			for (int unit : mapPlaceToUnits(place)) {
				result += String.format("\n  * Unit %d", unit);
			}
		} else {
			result += "\nNo place set";
		}

		try {
			boolean[] affinities = getAffinity();
			result += "\nLocality:";
			for (int i = 0; i < affinities.length; i++) {
				if (affinities[i]) {
					result += String.format("\n  * Unit %d (Place %d)", i,
							mapUnitToPlace(i));
				}
			}
		} catch (GetAffinityException e) {
			e.printStackTrace();
		}

		return result;
	}

	public abstract int getNumberOfPlaces();

	public abstract int mapUnitToPlace(int unit);

	public abstract int[] mapPlaceToUnits(int place);

	protected native void setAffinity(int[] physicalUnits)
			throws SetAffinityException;

	protected native boolean[] getAffinity() throws GetAffinityException;

	protected native int getThreadId();

	static {
		System.loadLibrary("Place");
	}
}