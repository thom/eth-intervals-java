package ch.ethz.hwloc;

/**
 * Place
 * 
 * @author Thomas Weibel
 */
public abstract class Place {
	private int place = -1;

	public Place() {
		// TODO: constructor
	}

	public Place(int place) {
		// TODO: constructor
	}

	public void set(int place) {
		// TODO: set
	}

	public int get() {
		// TODO: get
		return 0;
	}

	public String toString() {
		// TODO: toString
		return null;
	}

	private native void setAffinity(int[] physicalUnits);

	private native int[] getAffinity();

	private native int getThreadId();

	static {
		System.loadLibrary("Place");
	}
}