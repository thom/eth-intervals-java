package ch.ethz.util;

import ch.ethz.hwloc.Units;

public enum Machine {
	Mafushi, Marvin;

	public Units getUnits() {
		Units result = null;

		try {
			result = (Units) Class.forName("ch.ethz.hwloc." + name() + "Units")
					.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
