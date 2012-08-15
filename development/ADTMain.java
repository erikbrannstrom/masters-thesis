import java.util.*;
import weka.core.Instance;
import weka.core.*;
import weka.core.Capabilities.*;
import weka.filters.*;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.regex.*;

public class ADTMain
{

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Missing command line arguments.");
			System.exit(1);
		}
		String input = args[0];

		DataSource source = new DataSource(input);
		Instances data = source.getDataSet();

		// Create filter
		Instance rule = new DenseInstance(4);
		rule.setDataset(data);
		rule.setValue(0, "M");
		rule.setValue(1, 1);
		rule.setMissing(2);
		rule.setMissing(3);
		SimpleStreamFilter filter = new InstanceMatchFilter(rule);

		// Filter input data
		filter.setInputFormat(data);
		data = Filter.useFilter(data, filter);

		// Set class attribute if not already set, assumed to be the last
		if (data.classIndex() == -1) {
			data.setClassIndex(data.numAttributes() - 1);
		}

		// Find index of yes in class attribute
		weka.core.Attribute classAttribute = data.classAttribute();
		int yesValue = 0;
		for (int i = 0; i < classAttribute.numValues(); i++) {
			if ("yes".equals(classAttribute.value(i))) {
				yesValue = i;
				break;
			}
		}

		// Calculate the weight of class=yes instances
		double count = 0.0;
		for (int i = 0; i < data.numInstances(); i++) {
			weka.core.Instance instance = data.instance(i);
			if ((int)instance.value(classAttribute) == yesValue) {
				count += instance.weight();
			}
		}
		double weight = Math.round(data.sumOfWeights()/count)*1.0;

		// Create classifier
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		CostMatrix costMatrix = new CostMatrix(2);
		costMatrix.setElement(0, 1, 1.0);
		costMatrix.setElement(1, 0, weight);
		csc.setCostMatrix(costMatrix);
		J48 j48 = new J48();
		csc.setClassifier(j48);
		Instances partial = getRandomPartial(data, 0.7, false, 198732467539812L);
		Instances testData = getRandomPartial(data, 0.3, true, 198732467539812L);
		csc.buildClassifier(partial);

		System.out.println(j48);
		int corrects = ClassifierEvaluator.evaluate(j48, testData);
		corrects = ClassifierEvaluator.evaluate(j48, partial);

		// Create distinct set of instances
		InstanceComparator comp = new InstanceComparator(false);
		TreeSet<Instance> set = new TreeSet<Instance>(comp);
		for (int i = 0; i < data.numInstances(); i++) {
			set.add(data.instance(i));
		}
		Instances uniqueInstances = new Instances(testData, 0);
		for (Instance inst : set) {
			inst.setWeight(1);
			uniqueInstances.add(inst);
		}

		for (int i = 0; i < uniqueInstances.numInstances(); i++) {
			Instance inst = uniqueInstances.get(i);
			inst.setClassMissing();
			double[] probs = j48.distributionForInstance(inst);
			System.out.println(inst);
			System.out.println(probs[yesValue]);
		}
	}

	public static Instances getRandomPartial(Instances data, double ratio, boolean invert, long seed)
	{
		int partialSize = (int)(data.sumOfWeights()*ratio);
		System.out.println(partialSize);
		Instances copy = new Instances(data, 0, data.numInstances());
		Instances partial = new Instances(data, 0, data.numInstances());
		for (int i = 0; i < partial.numInstances(); i++) {
			partial.get(i).setWeight(0.0);
		}

		Random rand = new Random(seed);
		while (partialSize > 0) {
			int next = rand.nextInt((int)copy.sumOfWeights());
			int index = 0;
			while (next >= 0) {
				if (next < copy.get(index).weight()) {
					break;
				} else {
					next -= (int)copy.get(index).weight();
					index++;
				}
			}
			Instance fromCopy = copy.get(index);
			if (fromCopy.weight() >= 1) {
				Instance toCopy = partial.get(index);
				fromCopy.setWeight(fromCopy.weight()-1);
				toCopy.setWeight(toCopy.weight()+1);
				partialSize--;
			}
		}

		if (invert) {
			return copy;
		} else {
			return partial;
		}
	}

}