import java.util.*;

public class Instance
{
	private List<Attribute> attributes;
	private List<? extends Object> values;

	public Instance(List<Attribute> attributes, List<? extends Object> values)
	{
		this.attributes = attributes;
		this.values = values;
	}

	public List<Attribute> attributes()
	{
		return this.attributes;
	}

	public List<? extends Object> values()
	{
		return this.values;
	}

	public Object attributeValue(Attribute attribute)
	{
		int index = this.attributes.indexOf(attribute);
		if (index == -1) {
			throw new IndexOutOfBoundsException();
		} else {
			return this.values.get(index);
		}
	}

	public String toString()
	{
		return this.values.toString();
	}

}