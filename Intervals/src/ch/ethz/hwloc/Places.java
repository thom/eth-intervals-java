package ch.ethz.hwloc;

public abstract class Places {
	protected static class PlaceIDImpl extends PlaceID {
		public PlaceIDImpl(int id, String machine) {
			super(id, machine);
		}
	}

	public final String name;
	public final int length;
	public final int unitsLength;

	protected final PlaceID[] placeIDs;
	protected final int[][] places;
	protected final int[] units;

	public Places(String name, PlaceID[] placeIDs, int[][] places, int[] units) {
		this.name = name;
		this.placeIDs = placeIDs;
		this.places = places;
		this.units = units;
		this.length = places.length;
		this.unitsLength = units.length;
	}

	// Marvin: PlaceID[] placeIDs = { new PlaceIDImpl(0) }
	// Mafushi: PlaceID[] placeIDs = { new PlaceIDImpl(0), new PlaceIDImpl(1) }
	public PlaceID getPlaceID(int id) {
		return placeIDs[id % placeIDs.length];
	}

	public int[] get(int id) {
		return places[id % length];
	}

	public int getUnit(int id) {
		return units[id % unitsLength];
	}
}
