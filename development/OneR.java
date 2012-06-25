import java.util.*;

/**
 * Implementation of the 1-rule classifier.
 */
public class OneR
{
	private Tree<Rule> classifier;

	public void learn(List<Instance> instances, Attribute classAttribute)
	{
		if (instances.size() == 0) {
			System.out.println("No instances.");
			return;
		}

		List<Attribute> attributes = instances.get(0).attributes();
		Tree<Rule> rules = new Tree<Rule>();

		for (int attributeIndex = 0; attributeIndex < attributes.size(); attributeIndex++) {
			Attribute attribute = attributes.get(attributeIndex);
			if (attribute.equals(classAttribute)) {
				continue;
			}

			Map<Object, Map<Object, Integer>> valueClassCount = new HashMap<Object, Map<Object, Integer>>();
			for (Instance instance : instances) {
				Object value = instance.attributeValue(attribute);

				Map<Object, Integer> classes = valueClassCount.get(value);

				if (classes == null) {
					classes = new HashMap<Object, Integer>();
					valueClassCount.put(value, classes);
				}
				
				Integer count = classes.get(instance.attributeValue(classAttribute));
				if (count == null) {
					classes.put(instance.attributeValue(classAttribute), 1);
				} else {
					classes.put(instance.attributeValue(classAttribute), count+1);
				}
			}

			Tree<Rule> subtree = new Tree<Rule>();
			// Find most frequent class for each value and add rule
			for (Object value : valueClassCount.keySet()) {
				Map<Object, Integer> countMap = valueClassCount.get(value);
				int max = 0;
				int totalCount = 0;
				Object mostFrequentClassValue = null;
				for (Object classValue : countMap.keySet()) {
					if (countMap.get(classValue) > max) {
						mostFrequentClassValue = classValue;
						max = countMap.get(classValue);
					}
					totalCount += countMap.get(classValue);
				}
				subtree.addChild(new Rule(attribute, value, mostFrequentClassValue));
			}
			rules.addChild(subtree);
		}

		// Prune tree
		int maxErrors = Integer.MAX_VALUE;
		Tree<Rule> bestRules = null;
		for (int i = 0; i < rules.children().size(); i++) {
			Tree<Rule> subtree = rules.children().get(i);
			int treeErrors = 0;
			for (Tree<Rule> rule : subtree.children()) {
				for (Instance instance : instances) {
					// Check if rule matches, but classes are different
					if (rule.data().match(instance) && !rule.data().classValue().equals(instance.attributeValue(classAttribute))) {
						treeErrors++;
					}
				}
			}
			if (treeErrors < maxErrors) {
				maxErrors = treeErrors;
				bestRules = subtree;
			}
		}
		System.out.println(bestRules);

		this.classifier = bestRules;
	}

	public String classify(Instance instance)
	{
		return "class";
	}

}