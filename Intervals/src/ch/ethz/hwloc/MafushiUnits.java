package ch.ethz.hwloc;

public class MafushiUnits implements Units {
	private int[] units = { 0, 2, 4, 6, 1, 3, 5, 7 };
	private int[][] nodes = { { 0, 2, 4, 6 }, { 1, 3, 5, 7 } };

	@Override
	public int get(int id) {
		return units[id];
	}

	@Override
	public int size() {
		return units.length;
	}

	@Override
	public int[] getNode(int id) {
		return nodes[id];
	}

	@Override
	public int nodesSize() {
		return nodes.length;
	}

	@Override
	public int unitsPerNode() {
		return units.length / nodes.length;
	}
}
