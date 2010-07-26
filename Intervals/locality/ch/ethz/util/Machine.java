package ch.ethz.util;

import ch.ethz.hwloc.Places;

public enum Machine {
	Mafushi, Marvin;

	public Places getPlaces() {
		Places result = null;

		try {
			result = (Places) Class
					.forName("ch.ethz.hwloc." + name() + "Places")
					.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
