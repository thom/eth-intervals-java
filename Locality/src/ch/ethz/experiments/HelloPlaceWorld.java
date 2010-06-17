package ch.ethz.experiments;

import ch.ethz.hwloc.MarvinPlace;
import ch.ethz.hwloc.Place;
import ch.ethz.hwloc.SetAffinityException;

public class HelloPlaceWorld extends Thread {
	private Place place;
	private int number;

	public HelloPlaceWorld(int number) {
		this.place = createPlace();
		this.number = number;
	}

	public void run() {
		try {
			System.out.println(place + "\n");
			try {
				place.set(number);
			} catch (SetAffinityException e) {
				e.printStackTrace();
			}
			System.out.println(place + "\n");
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Place createPlace() {
		return new MarvinPlace();
	}

	public static void main(String[] args) {
		for (int i = 0; i < createPlace().getNumberOfPlaces(); i++) {
			new HelloPlaceWorld(i).start();
		}
	}
}
