package ch.ethz.hwloc;

public class PlaceID {
	public final int id;
	public final String name;

	public PlaceID(int id) {
		this.id = id;
		this.name = "place-" + id;
	}
}
