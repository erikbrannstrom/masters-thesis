
public class OccurrenceCounter<E>
{
	private List<E> items;
	private List<Integer> counters;

	public void increase(E item)
	{
		int index = this.items.indexOf(item);
		if (index == -1) {
			this.items.add(item);
			this.counters.add(0);
		} else {
			Integer counter = this.counters.get(index);
			counter = counter + 1;
			this.counters.set(index, counter);
		}
	}

	public int count(E item)
	{
		int index = this.items.indexOf(item);
		if (index == -1) {
			return 0;
		} else {
			return this.counters.get(index);
		}
	}

}