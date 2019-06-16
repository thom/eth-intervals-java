package ch.ethz.util;

import java.lang.instrument.Instrumentation;

public class SizeOf {
	private static Instrumentation instrumentation;

	private static String[] unit = { "bytes", "kB", "MB" };

	/**
	 * Callback method used by the Java VM to inject the
	 * java.lang.instrument.Instrument instance
	 */
	public static void premain(String args, Instrumentation inst) {
		instrumentation = inst;
	}

	/**
	 * Calls java.lang.instrument.Instrument.getObjectSize(object).
	 * 
	 * @param object
	 *            the object to size
	 * @return an implementation-specific approximation of the amount of storage
	 *         consumed by the specified object.
	 * 
	 * @see java#lang#instrument#Instrument#Instrumentation#getObjectSize(Object
	 *      objectToSize)
	 */
	public static long get(Object o) {
		return instrumentation.getObjectSize(o);
	}

	/**
	 * Calls java.lang.instrument.Instrument.getObjectSize(object) and returns
	 * size in human readable format.
	 * 
	 * @param object
	 *            the object to size
	 * @return a string representation of the size argument followed by bytes,
	 *         kB for kilobytes or MB for megabytes
	 */
	public static String getHumanReadable(Object o) {
		double dSize = get(o);
		int i;
		for (i = 0; i < 3; ++i) {
			if (dSize < 1024)
				break;
			dSize /= 1024;
		}

		return String.format("%f %s", dSize, unit[i]);
	}
}
