import java.util.*;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.*;
import weka.core.Capabilities.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.Remove;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.regex.*;

public class CBRPrepare
{
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Missing command line arguments.");
			System.exit(1);
		}
		String input = args[0];

		DataSource source = new DataSource(input);
		Instances data = source.getDataSet();

		// Remove useless attributes
		int numAttributesOriginal = data.numAttributes();
		RemoveUseless filterUseless = new RemoveUseless();
		filterUseless.setInputFormat(data);
		data = Filter.useFilter(data, filterUseless);

		Attribute clicks = data.attribute("Clicks");
		Attribute impressions = data.attribute("Impressions");

		if (clicks == null || impressions == null) {
		    System.out.println("Could not find attributes for clicks and/or impressions.");
		    System.exit(1);
		}

		Comparator<Instance> instanceComparator = new Comparator<Instance>() {
			public int compare(Instance a, Instance b) {
				double[] aValues = a.toDoubleArray();
				double[] bValues = b.toDoubleArray();
				for (int i = 0; i < aValues.length; i++) {
					Attribute attr = a.dataset().attribute(i);
					if (attr.name().equals("Clicks") || attr.name().equals("Impressions")) {
						continue;
					} else if (aValues[i] < bValues[i]) {
						return -1;
					} else if (aValues[i] > bValues[i]) {
						return 1;
					}
				}
				return 0;
			}
		};

		Collections.sort(data, instanceComparator);
		Instance lastInstance = null;
		for (int i = data.numInstances()-1; i >= 0; i--) {
			Instance inst = data.get(i);
			if (lastInstance == null || instanceComparator.compare(inst, lastInstance) != 0) {
				lastInstance = inst;
				continue;
			} else {
				// If instances are equal..
				inst.setValue(clicks, lastInstance.value(clicks)+inst.value(clicks));
				inst.setValue(impressions, lastInstance.value(impressions)+inst.value(impressions));
				data.delete(i+1);
				lastInstance = inst;
			}
		}

		// Add CTR% with 95% confidence interval, assuming a normal distribution
		Attribute ctrL = new Attribute("CTR-Low", data.numAttributes());
		data.insertAttributeAt(ctrL, data.numAttributes());
		Attribute ctrH = new Attribute("CTR-High", data.numAttributes());
		data.insertAttributeAt(ctrH, data.numAttributes());

		for (int i = 0; i < data.numInstances(); i++) {
		    Instance inst = data.instance(i);
		    double numClicks = inst.value(clicks);
		    double numImpressions = inst.value(impressions);
		    double mean = numClicks/numImpressions;
		    double s2 = ( numClicks*Math.pow(1.0-mean, 2) + (numImpressions-numClicks)*Math.pow(0.0-mean, 2) ) / (numImpressions-1);
		    double delta = 1.96*Math.sqrt(s2/numImpressions);
		    inst.setValue(ctrL, mean-delta);
		    inst.setValue(ctrH, mean+delta);
		}
		System.out.println(data);
	}

	protected static boolean instancesEqual(Instance a, Instance b)
	{
		for (int i = 0; i < a.numAttributes(); i++) {
			if (a.attribute(i).name().equals("Clicks") || a.attribute(i).name().equals("Impressions")) {
				continue;
			} else if (Math.abs(a.value(i)-b.value(i)) > 0.001) {
				return false;
			}
		}
		return true;
	}

}