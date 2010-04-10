package ch.ethz.intervals;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import erco.tsp.intervals.Tsp;

public class TestErco {
	@Test public void TspMap16z() throws IOException {		
		Tsp tsp = new Tsp();
		int[] tour = tsp.solve("test/erco/tsp/tspfiles/map16z");
		int[] expected = new int[] {
			0, 12, 10, 15, 7, 6, 4, 5, 1, 13, 14, 3, 2, 8, 11, 9, 0
		};
		Assert.assertArrayEquals(expected, tour);
	}	
}
