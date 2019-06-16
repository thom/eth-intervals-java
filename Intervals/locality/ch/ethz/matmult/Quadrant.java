package ch.ethz.matmult;

public enum Quadrant {
	Quadrant0, Quadrant1, Quadrant2, Quadrant3, None;

	public static Quadrant[][] matrix() {
		Quadrant[][] result = new Quadrant[2][2];
		result[0][0] = Quadrant0;
		result[0][1] = Quadrant1;
		result[1][0] = Quadrant2;
		result[1][1] = Quadrant3;
		return result;
	}
}
