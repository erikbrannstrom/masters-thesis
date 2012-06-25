

public class Rule
{
	public enum Comparator { EQUAL, LESS_THAN, GREATER_THAN }

	private Attribute attribute;
	private Object value;
	private Object classValue;
	private Comparator comparator;

	public Rule(Attribute attribute, Object value, Object classValue, Rule.Comparator comparator)
	{
		if (comparator != Rule.Comparator.EQUAL && !(value instanceof Comparable)) {
			throw new IllegalArgumentException("For less/greater than, attribute must be comparable.");
		}
		this.attribute = attribute;
		this.value = value;
		this.classValue = classValue;
		this.comparator = comparator;
	}

	public Rule(Attribute attribute, Object value, Object classValue)
	{
		this(attribute, value, classValue, Rule.Comparator.EQUAL);
	}

	public boolean match(Instance instance)
	{
		return this.match(instance.attributeValue(this.attribute));
	}

	public boolean match(Object obj)
	{
		if (this.comparator == Rule.Comparator.EQUAL) {
			return value.equals(obj);
		}
		if (! (obj instanceof Comparable)) {
			throw new IllegalArgumentException();
		}

		// We have already thrown an exception in case the values are not comparable
		@SuppressWarnings("unchecked")
		Comparable<Object> instanceValue = (Comparable<Object>)obj;
		@SuppressWarnings("unchecked")
		Comparable<Object> ruleValue = (Comparable<Object>)this.value;

		if (this.comparator == Rule.Comparator.LESS_THAN) {
			return instanceValue.compareTo(ruleValue) < 0;
		} else {
			return instanceValue.compareTo(ruleValue) > 0;
		}
	}

	public Object classValue()
	{
		return this.classValue;
	}

	public Attribute attribute()
	{
		return this.attribute;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.attribute.toString());
		switch (this.comparator) {
			case EQUAL:
				buffer.append("="); break;
			case LESS_THAN:
				buffer.append("<"); break;
			case GREATER_THAN:
				buffer.append(">"); break;
		}
		buffer.append(value.toString());
		buffer.append(" -> ");
		buffer.append(this.classValue.toString());
		return buffer.toString();
	}

}