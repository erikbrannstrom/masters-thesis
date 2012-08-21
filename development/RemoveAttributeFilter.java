import java.util.*;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.filters.*;

public class RemoveAttributeFilter extends Filter
{
	private List<String> attributes;
	private List<Integer> removedAttributes;

	public RemoveAttributeFilter(List<String> attributes)
	{
		this.attributes = attributes;
		this.removedAttributes = new LinkedList<Integer>();
	}

	public String globalInfo()
	{
		return "Remove attributes.";
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
		return inputFormat;
	}

	protected Instance process(Instance inst) {
		return inst;
	}

}