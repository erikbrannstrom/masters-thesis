import java.util.List;
/**
 * General description of a rule.
 *
 * A rule is either a single value and its comparator.
 */
public class Rule
{
	public enum Comparator {
		EQUAL ("="),
		NOT_EQUAL ("!="),
		LESS_THAN ("<"),
		LESS_THAN_OR_EQUAL ("<="),
		GREATER_THAN (">"),
		GREATER_THAN_OR_EQUAL (">=");

		private String display;

		Comparator(String display) {
			this.display = display;
		}

		public String toString()
		{
			return this.display;
		}

		public static Comparator fromString(String comp)
		{
			for (Comparator c : Comparator.values()) {
				if (c.toString().equals(comp)) {
					return c;
				}
			}
			return null;
		}
	}

	private Object value;
	private Comparator comparator;

	public Rule(Object value, Rule.Comparator comparator)
	{
		if (comparator != Rule.Comparator.EQUAL && comparator != Rule.Comparator.NOT_EQUAL && !(value instanceof Comparable)) {
			throw new IllegalArgumentException("For less/greater than, attribute must be comparable.");
		}
		this.value = value;
		this.comparator = comparator;
	}

	public Rule(Object value)
	{
		this(value, Rule.Comparator.EQUAL);
	}

	public Object value()
	{
		return this.value;
	}

	public Rule.Comparator comparator()
	{
		return this.comparator;
	}

	public boolean match(Object obj)
	{
		if (this.comparator == Rule.Comparator.EQUAL) {
			return value.equals(obj);
		} else if (this.comparator == Rule.Comparator.NOT_EQUAL) {
			return !value.equals(obj);
		}
		if (! (obj instanceof Comparable)) {
			throw new IllegalArgumentException();
		}

		// We have already thrown an exception in case the values are not comparable
		@SuppressWarnings("unchecked")
		Comparable<Object> compareValue = (Comparable<Object>)obj;
		@SuppressWarnings("unchecked")
		Comparable<Object> ruleValue = (Comparable<Object>)this.value;

		switch (this.comparator) {
			case LESS_THAN:
				return compareValue.compareTo(ruleValue) < 0;
			case LESS_THAN_OR_EQUAL:
				return compareValue.compareTo(ruleValue) <= 0;
			case GREATER_THAN:
				return compareValue.compareTo(ruleValue) > 0;
			case GREATER_THAN_OR_EQUAL:
				return compareValue.compareTo(ruleValue) >= 0;
		}

		return false;
	}

	public boolean coveredBy(Rule rule)
	{
		if (! (this.value instanceof Comparable)) {
			// Non-comparable values
			if (this.comparator() == Comparator.EQUAL && rule.comparator() == Comparator.EQUAL) {
				return this.value().equals(rule.value());
			} else if (this.comparator() == Comparator.NOT_EQUAL && rule.comparator() == Comparator.NOT_EQUAL) {
				return !this.value().equals(rule.value());
			} else {
				return false;
			}
		} else {
			// Comparable values
			@SuppressWarnings("unchecked")
			Comparable<Object> ruleValue = (Comparable<Object>)rule.value();
			@SuppressWarnings("unchecked")
			Comparable<Object> value = (Comparable<Object>)this.value;

			if (this.comparator() == Comparator.EQUAL) {
				return rule.match(value);
			} else if (this.comparator() == Comparator.NOT_EQUAL) { 
				return !this.value().equals(ruleValue);
			} else if (this.comparator() == Comparator.LESS_THAN) {
				if (rule.comparator() == Comparator.LESS_THAN || rule.comparator() == Comparator.LESS_THAN_OR_EQUAL) {
					return value.compareTo(ruleValue) <= 0;
				} else {
					return false;
				}
			} else if (this.comparator() == Comparator.LESS_THAN_OR_EQUAL) {
				if (rule.comparator() == Comparator.LESS_THAN) {
					return value.compareTo(ruleValue) < 0;
				} else if (rule.comparator() == Comparator.LESS_THAN_OR_EQUAL) {
					return value.compareTo(ruleValue) <= 0;
				} else {
					return false;
				}
			} else if (this.comparator() == Comparator.GREATER_THAN) {
				if (rule.comparator() == Comparator.GREATER_THAN || rule.comparator() == Comparator.GREATER_THAN_OR_EQUAL) {
					return value.compareTo(ruleValue) >= 0;
				} else {
					return false;
				}
			} else if (this.comparator() == Comparator.GREATER_THAN_OR_EQUAL) {
				if (rule.comparator() == Comparator.GREATER_THAN) {
					return value.compareTo(ruleValue) > 0;
				} else if (rule.comparator() == Comparator.GREATER_THAN_OR_EQUAL) {
					return value.compareTo(ruleValue) >= 0;
				} else {
					return false;
				}
			}
		}
		System.out.println("Unsuccessful evaluation in coveredBy.");
		return false;
	}

	/**
	 * Returns a string representation of the rule.
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.comparator.toString()).append(" ").append(this.value.toString());
		return buffer.toString();
	}

}