
public class Pair<E, F>
{
	private E first;
	private F second;

	public Pair(E first, F second)
	{
		this.first = first;
		this.second = second;
	}

	public E first()
	{
		return this.first;
	}

	public F second()
	{
		return this.second;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Pair) {
			Pair<E, F> cmp = (Pair<E, F>)obj;
			return this.first().equals(cmp.first()) && this.second().equals(cmp.second());
		} else {
			return false;
		}
	}

}