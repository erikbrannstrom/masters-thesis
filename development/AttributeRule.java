
public class AttributeRule
{
	private Attribute attribute;
	private Rule rule;

	public AttributeRule(Attribute attribute, Rule rule)
	{
		this.attribute = attribute;
		this.rule = rule;
	}

	public Attribute attribute()
	{
		return this.attribute;
	}

	public Rule rule()
	{
		return this.rule;
	}

	public boolean coveredBy(AttributeRule match)
	{
		if (this.attribute().equals(match.attribute())) {
			return this.rule().coveredBy(match.rule());
		}
		return false;
	}

	public String toString()
	{
		return this.attribute.name() + " " + this.rule().toString();
	}

}