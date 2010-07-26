package ch.ethz.hwloc;

public class PlacesTest {
	public static void main(String[] args) {
		Places places = new MafushiPlaces();
		System.out.println(places.name);
		System.out.println(places.length);
		System.out.println(places.unitsLength);
		System.out.println(places.getUnit(0));
		System.out.println(places.getPlaceID(0));
		System.out.println(places.getPlaceID(1));		
	}
}
