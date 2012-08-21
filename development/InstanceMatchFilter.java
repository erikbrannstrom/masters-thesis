import java.util.*;
import weka.core.Instance;
import weka.core.*;
import weka.core.Capabilities.*;
import weka.filters.*;

public class InstanceMatchFilter extends SimpleStreamFilter
{
	private Instance rule;
	private boolean removeAttributes;
	private List<Integer> removedAttributes;

	/**
	 * Create a new instance match filter. All input instances will be compared
	 * to the rule instance used for initialization, and only those whose values
	 * match exactly for non-missing values are kept.
	 */
	public InstanceMatchFilter(Instance rule, boolean removeAttributes)
	{
		this.rule = rule;
		this.removeAttributes = removeAttributes;
		this.removedAttributes = new LinkedList<Integer>();
	}

	public String globalInfo()
	{
		return "Remove instances that do not match the specified instance.";
	}

	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.enableAllAttributes();
		result.enableAllClasses();
		result.enable(Capability.NO_CLASS);
		return result;
	}

	protected Instances determineOutputFormat(Instances inputFormat)
	{
		Instances result = new Instances(inputFormat, 0);
		if (!this.removeAttributes) {
			return result;
		}

		for (int i = this.rule.numAttributes()-1; i >= 0; i--) {
			if (!this.rule.isMissing(i)) {
				this.removedAttributes.add(i);
			}
		}
		for (int index : this.removedAttributes) {
			 result.deleteAttributeAt(index);
		}
		return result;
	}

	protected Instance process(Instance inst) {
		double[] values = new double[inst.numAttributes() - this.removedAttributes.size()];
		int ref = 0;
		int matchingAttributes = 1;
		for (int n = 0; n < inst.numAttributes(); n++) {
			if (this.rule.isMissing(n)) {
				values[ref] = inst.value(n);
				ref++;
			} else if (this.rule.value(n) != inst.value(n)) {
				// If values do not match, we don't keep the instance
				return null;
			} else if (!this.removeAttributes) {
				values[ref] = inst.value(n);
				ref++;
			}
		}
		Instance result = new DenseInstance(inst.weight(), values);
		return result;
	}

}