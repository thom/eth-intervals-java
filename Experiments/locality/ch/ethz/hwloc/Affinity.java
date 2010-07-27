package ch.ethz.hwloc;

/**
 * Affinity
 * 
 * @author Thomas Weibel
 */
public class Affinity {
	/**
	 * Sets affinity of the current thread to a physical unit
	 * 
	 * @param physicalUnit
	 * @throws SetAffinityException
	 */
	public static native void set(int physicalUnit) throws SetAffinityException;

	/**
	 * Sets affinity of current thread to an array of physical units
	 * 
	 * @param physicalUnits
	 * @throws SetAffinityException
	 */
	public static native void set(int[] physicalUnits)
			throws SetAffinityException;

	/**
	 * Returns a boolean array whose elements are set to true if the current
	 * thread has an affinity set to the corresponding physical unit
	 * 
	 * @return boolean array of physical units the current thread has an
	 *         affinity to
	 * @throws GetAffinityException
	 */
	public static native boolean[] get() throws GetAffinityException;

	/**
	 * Returns the native thread ID of the thread calling this method
	 * 
	 * @return current native thread ID
	 */
	public static native int getThreadId();

	/**
	 * Returns affinity and thread ID of current thread for debugging purposes
	 * 
	 * @return information about current thread
	 */
	public static String getInformation() {
		String result = String.format("Thread ID: %d", getThreadId());

		try {
			boolean[] affinities = get();
			result += "\nAffinity:";
			for (int i = 0; i < affinities.length; i++) {
				if (affinities[i]) {
					result += String.format("\n  * Physical Unit %d", i);
				}
			}
		} catch (GetAffinityException e) {
			e.printStackTrace();
		}

		return result;
	}

	static {
		System.loadLibrary("Affinity");
	}
}