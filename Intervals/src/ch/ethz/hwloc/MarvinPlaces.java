package ch.ethz.hwloc;

public class MarvinPlaces extends Places {
	private static String marvinName = "marvin";
	private static PlaceID[] marvinPlaceIDs = { new PlaceIDImpl(0, marvinName) };
	private static int[][] marvinPlaces = { { 0, 1 } };
	private static int[] marvinUnits = { 0, 1 };

	public MarvinPlaces() {
		super(marvinName, marvinPlaceIDs, marvinPlaces, marvinUnits);
	}
}
