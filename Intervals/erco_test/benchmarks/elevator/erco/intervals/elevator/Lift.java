package erco.intervals.elevator;
/*
 * Copyright (C) 2000 by ETHZ/INF/CS
 * All rights reserved
 * 
 * @version $Id: Lift.java 2094 2003-01-30 09:41:18Z praun $
 * @author Roger Karrer
 */

import java.util.Enumeration;
import java.util.Vector;

import ch.ethz.intervals.Dependency;
import ch.ethz.intervals.Interval;
import ch.ethz.intervals.Intervals;
import ch.ethz.intervals.Lock;
import ch.ethz.intervals.quals.Creator;

@Creator("this.lock")
class Lift {

	// used for assigning unique identifiers to the elevators
	private static int count = 0;

	public static final int IDLE = 0;
	public static final int UP = 1;
	public static final int DOWN = 2;
	
	/** The lock is acquired by all intervals pertaining to this lift. 
	 *  It shouldn't really be necessary as (at the moment, anyway) there
	 *  can only be one of those at a time. */
	private final Lock lock;
	
	private final String name;
	
	private int travelDir; // one of IDLE, UP, or DOWN
	private int currentFloor;
	
	// holds the number of people who want to get off on each floor
	private final int[] peopleFor;
	
	// Values in pickupOn can be IDLE, UP, DOWN, and UP|DOWN, which indicate
	// which calls the elevator should respond to on each floor. IDLE means
	// don't pick up on that floor
	private final int[] pickupOn;
	private int firstFloor, lastFloor;
	
	// reference to the shared control object
	private final Controls controls;

	// Create a new elevator that can travel from floor 1 to floor numFloors.
	// The elevator starts on floor 1, and is initially idle with no passengers.
	// c is a reference to the shared control object
	// The thread starts itself
	public Lift(int numFloors, Controls c) {
		name = "Lift " + count++;
		lock = new Lock();
		controls = c;
		firstFloor = 1;
		lastFloor = numFloors;
		travelDir = IDLE;
		currentFloor = firstFloor;
		pickupOn = new int[numFloors + 1];
		peopleFor = new int[numFloors + 1];
		for (int i = 0; i <= numFloors; i++) {
			pickupOn[i] = IDLE;
			peopleFor[i] = 0;
		}
	}
	
	public void start(Interval parent) {		
		nextLiftInterval(parent);		
	}
	
	// IDLE
	// First check to see if there is an up or down call on what ever floor
	// the elevator is idle on. If there isn't one, then check the other floors.
	private final class LiftIdleInterval extends Interval {

		public LiftIdleInterval(Dependency dep) {
			super(dep, name + "-IDLE");
			Intervals.addExclusiveLock(this, lock);
		}

		@Override
		protected void run() {
			boolean foundFloor = false;
			int targetFloor = -1;

			if (controls.claimUp(name, currentFloor)) {
				// System.out.println("Lift::doIdle - could claim upcall on current floor");
				// // CARE
				foundFloor = true;
				targetFloor = currentFloor;
				travelDir = UP;
				addPeople(controls.getUpPeople(currentFloor));
			} else if (controls.claimDown(name, currentFloor)) {
				// System.out.println("Lift::doIdle - could claim downcall on current floor");
				// // CARE
				foundFloor = true;
				targetFloor = currentFloor;
				travelDir = DOWN;
				addPeople(controls.getDownPeople(currentFloor));
			}

			// System.out.println("Lift::doIdle - lookuing for calls on other floors");
			// // CARE
			for (int floor = firstFloor; !foundFloor && floor <= lastFloor; floor++) {
				// System.out.println("Lift::doIdle - checking floor " + floor); //
				// CARE
				if (controls.claimUp(name, floor)) {
					// System.out.println("Lift::doIdle - success with claimUp " +
					// floor); // CARE
					foundFloor = true;
					targetFloor = floor;
					pickupOn[floor] |= UP;
					travelDir = (targetFloor > currentFloor) ? UP : DOWN;
				} else if (controls.claimDown(name, floor)) {
					// System.out.println("Lift::doIdle - success with claimDown " +
					// floor); // CARE
					foundFloor = true;
					targetFloor = floor;
					pickupOn[floor] |= DOWN;
					travelDir = (targetFloor > currentFloor) ? UP : DOWN;
				}
			}

			if (foundFloor) {
				System.out.println(name + " is now moving "
						+ ((travelDir == UP) ? "UP" : "DOWN"));
			}
			
			if(foundFloor || !controls.terminated())
				nextLiftInterval(parent);
		}
		
	}

	// MOVING
	// First change floor (up or down as appropriate)
	// Drop off passengers if we have to
	// Then pick up passengers if we have to
	private final class LiftMovingInterval extends Interval {
		
		public LiftMovingInterval(Dependency dep) {
			super(dep, name + "-MOVING");
			Intervals.addExclusiveLock(this, lock);
		}

		@Override
		protected void run() {
			currentFloor += (travelDir == UP) ? 1 : -1;
			int oldDir = travelDir;
	
			if (travelDir == UP && currentFloor == lastFloor)
				travelDir = DOWN;
			if (travelDir == DOWN && currentFloor == firstFloor)
				travelDir = UP;
			System.out.println(name + " now on " + currentFloor);
	
			if (peopleFor[currentFloor] > 0) {
				System.out.println(name + " delivering "
						+ peopleFor[currentFloor] + " passengers on "
						+ currentFloor);
				peopleFor[currentFloor] = 0;
			}
	
			// Pickup people who want to go up if:
			// 1) we previous claimed an up call on this floor, or
			// 2) we are travelling up and there is an unclaimed up call on this
			// floor
			if (((pickupOn[currentFloor] & UP) != 0)
					|| (travelDir == UP && controls
							.claimUp(name, currentFloor))) {
				addPeople(controls.getUpPeople(currentFloor));
				pickupOn[currentFloor] &= ~UP;
			}
	
			// Pickup people who want to go down if:
			// 1) we previous claimed an down call on this floor, or
			// 2) we are travelling down and there is an unclaimed down call on this
			// floor
			if (((pickupOn[currentFloor] & DOWN) != 0)
					|| (travelDir == DOWN && controls.claimDown(name, currentFloor))) {
				addPeople(controls.getDownPeople(currentFloor));
				pickupOn[currentFloor] &= ~DOWN;
			}
	
			if (travelDir == UP) {
				// If we are travelling up, and there are people who want to get off
				// on a floor above this one, continue to go up.
				if (stopsAbove())
					;
				else {
					// If we are travelling up, but no one wants to get off above
					// this
					// floor, but they do want to get off below this one, start
					// moving down
					if (stopsBelow())
						travelDir = DOWN;
					// Otherwise, no one is the elevator, so become idle
					else
						travelDir = IDLE;
				}
			} else {
				// If we are travelling down, and there are people who want to get
				// off
				// on a floor below this one, continue to go down.
				if (stopsBelow())
					;
				else {
					// If we are travelling down, but no one wants to get off below
					// this
					// floor, but they do want to get off above this one, start
					// moving up
					if (stopsAbove())
						travelDir = UP;
					// Otherwise, no one is the elevator, so become idle
					else
						travelDir = IDLE;
				}
			}
	
			// Print out are new direction
			if (oldDir != travelDir) {
				System.out.print(name);
				if (travelDir == IDLE)
					System.out.println(" becoming IDLE");
				else if (travelDir == UP)
					System.out.println(" changing to UP");
				else if (travelDir == DOWN)
					System.out.println(" changing to DOWN");
			}
			
			nextLiftInterval(parent);
		}
	}

	// Body of the thread. If the elevator is idle, it checks for calls
	// every tenth of a second. If it is moving, it takes 1 second to
	// move between floors.
	private void nextLiftInterval(Interval parent) {
		while (true) {
			if (travelDir == IDLE) {
				new /*@ch.ethz.intervals.Parent("parent")*/ LiftIdleInterval(parent);
			} else {
				new /*@ch.ethz.intervals.Parent("parent")*/ LiftMovingInterval(parent);
			}
		}
	}

	// Returns true if there are passengers in the elevator who want to stop
	// on a floor above currentFloor, or we claimed a call on a floor below
	// currentFloor
	private boolean stopsAbove() {
		boolean above = false;
		for (int i = currentFloor + 1; !above && i <= lastFloor; i++)
			above = (pickupOn[i] != IDLE) || (peopleFor[i] != 0);
		return above;
	}

	// Returns true if there are passengers in the elevator who want to stop
	// on a floor below currentFloor, or we claiemda call on a floor above
	// currentFloor
	private boolean stopsBelow() {
		boolean below = false;
		for (int i = currentFloor - 1; !below && (i >= firstFloor); i--)
			below = (pickupOn[i] != IDLE) || (peopleFor[i] != 0);
		return below;
	}

	// Updates peopleFor based on the Vector of destination floors received
	// from the control object
	private void addPeople(Vector<Integer> people) {
		System.out.println(name + " picking up " + people.size()
				+ " passengers on " + currentFloor);
		for (Enumeration<Integer> e = people.elements(); e.hasMoreElements();) {
			int toFloor = (e.nextElement()).intValue();
			peopleFor[toFloor] += 1;
		}
	}
}
