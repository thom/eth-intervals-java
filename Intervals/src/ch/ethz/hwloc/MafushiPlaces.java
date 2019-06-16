package ch.ethz.hwloc;

public class MafushiPlaces extends Places {
	private static String name = "mafushi";
	private static PlaceID[] placeIDs = { new PlaceIDImpl(0, name),
			new PlaceIDImpl(1, name) };
	private static int[][] places = { { 0, 2, 4, 6 }, { 1, 3, 5, 7 } };
	private static int[] units = { 0, 2, 4, 6, 1, 3, 5, 7 };

	public MafushiPlaces() {
		super(name, placeIDs, places, units);
	}
}
