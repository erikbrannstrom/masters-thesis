import java.util.Arrays;
import java.util.List;

public class NumericAttribute extends Attribute
{
	private boolean bounded;
	private double lowerBound, upperBound;

	public NumericAttribute(String name)
	{
		super(name);
		this.bounded = false;
	}

	public NumericAttribute(String name, double lowerBound, double upperBound)
	{
		this(name);
		this.bounded = true;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public boolean validate(Object value)
	{
		if (value instanceof Number) {
			if (this.bounded) {
				double val = ((Number)value).doubleValue();
				return (val <= this.upperBound && val >= this.lowerBound);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.name());
		if (this.bounded) {
			buffer.append(" (").append(this.lowerBound).append(" <= x <= ").append(this.upperBound).append(")");
		}
		return buffer.toString();
	}

}