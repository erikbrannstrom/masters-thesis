
public class Attribute
{
	private String name;
	private Class<?> type;

	public Attribute(String name, Class<?> type)
	{
		this.name = name;
		this.type = type;
	}

	public String name()
	{
		return this.name;
	}

	public Class<?> type()
	{
		return this.type;
	}

	public String toString()
	{
		return this.name;
	}

}