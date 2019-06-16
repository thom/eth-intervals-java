package ch.ethz.hwloc;

public abstract class PlaceID {
	public final int id;
	public final String name;

	public PlaceID(int id, String machine) {
		this.id = id;
		this.name = machine + "-place-" + id;
	}

	public String toString() {
		return name;
	}
}
