package ch.ethz.hwloc;

/**
 * Place implementation for marvin
 * 
 * @author Thomas Weibel
 */
public class MarvinPlace extends Place {
	// 0 -> 0
	// 1 -> 1
	private final int[] mapUnitToPlace = { 0, 1 };

	// 0 -> { 0 }
	// 1 -> { 1 }
	private final int[][] mapPlaceToUnits = { { 0 }, { 1 } };

	public MarvinPlace() {
		super();
	}

	@Override
	public void set(int place) throws SetAffinityException {
		setAffinity(mapPlaceToUnits[place]);
		super.set(place);
	}

	@Override
	public int mapUnitToPlace(int unit) {
		return mapUnitToPlace[unit];
	}

	@Override
	public int[] mapPlaceToUnits(int place) {
		return mapPlaceToUnits[place];
	}

	@Override
	public int getNumberOfPlaces() {
		return mapPlaceToUnits.length;
	}
}
