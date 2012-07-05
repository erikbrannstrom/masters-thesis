
public class Attribute
{
	private String name;

	public Attribute(String name)
	{
		this.name = name;
	}

	public String name()
	{
		return this.name;
	}

	/**
	 * Check whether or not an object is a valid value for this attribute type.
	 */
	//public abstract boolean validate(Object value);

	/**
	 * Attributes are uniquely identified by their name.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Attribute) {
			return ((Attribute)obj).name().equals(this.name());
		} else {
			return false;
		}
	}

	public String toString()
	{
		return this.name;
	}

}