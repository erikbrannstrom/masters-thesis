import java.util.*;

/**
 * Implementation of the Yale Sparse Matrix Format.
 */
public class SparseMatrix<E>
{
	private int rows;
	private int columns;
	private List<E> values;
	private List<Integer> rowIndexes;
	private List<Integer> columnIndexes;
	private E defaultValue;

	public SparseMatrix(int rows, int columns, E defaultValue)
	{
		this.rows = rows;
		this.columns = columns;
		this.values = new LinkedList<E>();
		this.rowIndexes = new ArrayList<Integer>();
		for (int i = 0; i < this.rows+1; i++) {
			this.rowIndexes.add(0);
		}
		this.columnIndexes = new LinkedList<Integer>();
		this.defaultValue = defaultValue;
	}

	public void set(E value, int row, int col)
	{
		if (row < 0 || row >= this.rows || col < 0 || col >= this.columns) {
			throw new IndexOutOfBoundsException();
		}

		for (int i = row+1; i < this.rowIndexes.size(); i++) {
			this.rowIndexes.set(i, this.rowIndexes.get(i)+1);
		}

		this.columnIndexes.add(this.rowIndexes.get(row), col);
		this.values.add(this.rowIndexes.get(row), value);
	}

	public E get(int row, int col)
	{
		if (row < 0 || row > this.rows || col < 0 || col > this.columns) {
			throw new IndexOutOfBoundsException();
		}

		int numValuesInRow = this.rowIndexes.get(row+1) - this.rowIndexes.get(row);
		if (numValuesInRow == 0) {
			return this.defaultValue;
		}

		for (int i = this.rowIndexes.get(row); i < this.rowIndexes.get(row+1); i++) {
			if (this.columnIndexes.get(i) == col) {
				return this.values.get(i);
			}
		}

		return this.defaultValue;
	}

	public E defaultValue()
	{
		return this.defaultValue;
	}

	public List<E> allValues()
	{
		return Collections.unmodifiableList(this.values);
	}

	public int rows()
	{
		return this.rows;
	}

	public int columns()
	{
		return this.columns;
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		for (int row = 0; row < this.rows; row++) {
			for (int col = 0; col < this.columns; col++) {
				str.append(this.get(row, col).toString()).append("  ");
			}
			str.append("\n");
		}
		return str.toString();
	}

}