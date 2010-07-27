package ch.ethz.experiments;

public class MergeSortHierarchyTest {

	public static void main(String[] args) {
		String[] pred = { "Sorter 0", "Sorter 1", "Sorter 2", "Sorter 3",
				"Sorter 4", "Sorter 5", "Sorter 6", "Sorter 7" };
		for (String sorter : pred) {
			System.out.print(" (" + sorter + ") ");
		}
		System.out.println();
		foobar(pred.length / 2, pred);
	}

	public static void foobar(int nr, String[] pred) {
		String[] newPred = new String[nr];
		if (!(nr == 0)) {
			for (int i = 0; i < nr; i++) {
				System.out.printf(" (%s, %s) ", pred[2 * i], pred[(2 * i) + 1]);
				newPred[i] = "Merger " + i;
			}
			System.out.println();
			foobar(nr / 2, newPred);
		}
	}
}
