package ch.ethz.experiments;

import ch.ethz.hwloc.Affinity;
import ch.ethz.hwloc.MarvinUnits;
import ch.ethz.hwloc.SetAffinityException;
import ch.ethz.hwloc.Units;

public class HelloAffinityWorld extends Thread {
	private int id;

	public HelloAffinityWorld(int id) {
		this.id = id;
	}

	public void run() {
		try {
			System.out.println(Affinity.getInformation() + "\n");
			try {
				Affinity.set(units.get(id));
			} catch (SetAffinityException e) {
				e.printStackTrace();
			}
			System.out.println(Affinity.getInformation() + "\n");
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Units units = new MarvinUnits();

	public static void main(String[] args) {
		for (int i = 0; i < units.size(); i++) {
			new HelloAffinityWorld(i).start();
		}
	}
}
