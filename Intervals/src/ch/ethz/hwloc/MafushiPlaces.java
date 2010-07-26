package ch.ethz.hwloc;

public class MafushiPlaces extends Places {
	private static String mafushiName = "mafushi";
	private static PlaceID[] mafushiPlaceIDs = {
			new PlaceIDImpl(0, mafushiName), new PlaceIDImpl(1, mafushiName) };
	private static int[][] mafushiPlaces = { { 0, 2, 4, 6 }, { 1, 3, 5, 7 } };
	private static int[] mafushiUnits = { 0, 2, 4, 6, 1, 3, 5, 7 };

	public MafushiPlaces() {
		super(mafushiName, mafushiPlaceIDs, mafushiPlaces, mafushiUnits);
	}
}
