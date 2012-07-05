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

	/**
	 * Remove rules that do not add information, e.g. rule set
	 * x > 3 & x > 5 can be simplified to x > 5.
	 */
	public void removeRedundancy()
	{
		List<AttributeRule> modifiedData = new LinkedList<AttributeRule>(this.data);
		for (AttributeRule rule1 : this.data) {
			for (AttributeRule rule2 : this.data) {
				if (rule1 == rule2) {
					continue;
				} else if (rule1.coveredBy(rule2)) {
					modifiedData.remove(rule2);
				}
			}
		}
		this.data = modifiedData;
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