package ch.ethz.hwloc;

/**
 * Place implementation for mafushi
 * 
 * @author Thomas Weibel
 */
public class MafushiPlace extends Place {
	// 0 -> 0
	// 1 -> 4
	// 2 -> 1
	// 3 -> 5
	// 4 -> 2
	// 5 -> 6
	// 6 -> 3
	// 7 -> 7
	private final int[] mapUnitToPlace = { 0, 4, 1, 5, 2, 6, 3, 7 };

	// 0 -> { 0 }
	// 1 -> { 2 }
	// 2 -> { 4 }
	// 3 -> { 6 }
	// 4 -> { 1 }
	// 5 -> { 3 }
	// 6 -> { 5 }
	// 7 -> { 7 }
	private final int[][] mapPlaceToUnits = { { 0 }, { 2 }, { 4 }, { 6 },
			{ 1 }, { 3 }, { 5 }, { 7 } };

	public MafushiPlace() {
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
