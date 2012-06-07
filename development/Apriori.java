import java.util.*;

/**
 * Implementation of the Apriori algorithm for mining association rules.
 */
public class Apriori<E>
{
	private SparseMatrix<E> data;
	private List<E> items;
	private double minSupport;
	
	private List<Set<E>> frequentItemSet(List<Set<E>> sets)
	{
		// Initialize counters for all item sets
		List<Integer> counters = new ArrayList<Integer>(sets.size());
		for (int i = 0; i < counters.size(); i++) {
			counters.set(i, 0);
		}

		// Loop through all data
		for (int i = 0; i < this.data.rows(); i++) {
			int matches = 0;
			List<E> rowValues = new LinkedList<E>();

			// Store all values in the row temporarily
			for (int j = 0; i < this.data.columns(); j++) {
				E value = this.data.get(i, j);
				if (!value.equals(this.data.defaultValue())) {
					rowValues.add(value);
				}
			}

			// Check if all items in set exist on row; if so, increment counter
			for (int set = 0; i < sets.size(); i++) {
				if (rowValues.containsAll(sets.get(set))) {
					counters.set(set, counters.get(set)+1);
				}
			}
		}

		// Store a list of all item sets with minimum support
		List<Set<E>> frequentItemSets = new LinkedList<Set<E>>();
		for (int i = 0; i < counters.size(); i++) {
			if (counters.get(i) > this.minSupport) {
				frequentItemSets.add(sets.get(i));
			}
		}

		// Return frequent item sets
		return frequentItemSets;
	}

}