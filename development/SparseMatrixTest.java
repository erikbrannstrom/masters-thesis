/**
 * Test SparseMatrix.
 */
public class SparseMatrixTest
{

	public static void main(String[] args)
	{
		SparseMatrix<Integer> matrix = new SparseMatrix<Integer>(4, 5, new Integer(0));
		matrix.set(1, 1, 1);
		matrix.set(2, 1, 3);
		matrix.set(3, 2, 4);
		matrix.set(4, 3, 2);
		matrix.set(5, 3, 3);
		Integer result = matrix.get(3, 2);
		if (result != 4) {
			System.out.println("(3, 2) should be 4.");
		}
		result = matrix.get(1, 3);
		if (result != 2) {
			System.out.println("(1, 3) should be 2.");
		}
		result = matrix.get(3, 3);
		if (result != 5) {
			System.out.println("(3, 3) should be 5.");
		}

		System.out.println(matrix.toString());
	}

}