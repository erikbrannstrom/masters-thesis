import java.util.*;

public class AnalysisModelEntry implements Iterable<AttributeRule>
{
	private List<AttributeRule> data;

	public AnalysisModelEntry()
	{
		this.data = new LinkedList<AttributeRule>();
	}

	public AnalysisModelEntry(Attribute attribute, Rule rule)
	{
		this();
		this.data.add(new AttributeRule(attribute, rule));
	}

	public AnalysisModelEntry(List<AttributeRule> list)
	{
		this.data = list;
	}

	public Iterator<AttributeRule> iterator()
	{
		return this.data.iterator();
	}

	public int size()
	{
		return this.data.size();
	}

	public void add(AttributeRule entry)
	{
		this.data.add(entry);
	}

	public AttributeRule get(int index)
	{
		return this.data.get(index);
	}

	public boolean matchedBy(List<AttributeRule> rules)
	{
		for (AttributeRule rule : rules) {
			boolean attributeFound = false, matched = false;
			for (AttributeRule match : this.data) {
				if (rule.attribute().equals(match.attribute())) {
					attributeFound = true;
					if (rule.coveredBy(match)) {
						matched = true;
						break;
					}
				}
			}
			if (attributeFound && !matched) {
				return false;
			}
		}
		return true;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		for (AttributeRule attRule : this.data) {
			buffer.append(attRule.toString()).append(" & ");
		}
		buffer.delete(buffer.length()-3, buffer.length());

		return buffer.toString();
	}

}