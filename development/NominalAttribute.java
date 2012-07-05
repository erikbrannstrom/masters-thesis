import java.util.Arrays;
import java.util.List;

public class NominalAttribute extends Attribute
{
	private List<String> values;

	public NominalAttribute(String name, List<String> values)
	{
		super(name);
		this.values = values;
	}

	public NominalAttribute(String name, String... values)
	{
		this(name, Arrays.asList(values));
	}

	public boolean validate(Object value)
	{
		if (value instanceof String) {
			return this.values.contains((String)value);
		} else {
			return false;
		}
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.name()).append(" { ");
		for (String val : this.values) {
			buffer.append(val).append(", ");
		}
		buffer.delete(buffer.length()-2, buffer.length()).append(" }");
		return buffer.toString();
	}

}