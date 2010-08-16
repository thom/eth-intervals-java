package ch.ethz.hwloc;

public class MarvinPlaces extends Places {
	private static String name = "marvin";
	private static PlaceID[] placeIDs = { new PlaceIDImpl(0, name) };
	private static int[][] places = { { 0, 1 } };
	private static int[] units = { 0, 1 };

	public MarvinPlaces() {
		super(name, placeIDs, places, units);
	}
}
