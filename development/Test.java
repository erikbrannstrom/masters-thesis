import java.util.*;

public class Test
{
	
	public static void main(String[] args)
	{
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("outlook", String.class));
		Attribute temp = new Attribute("temperature", Integer.TYPE);
		attributes.add(temp);
		Attribute humidity = new Attribute("humidity", Integer.TYPE);
		attributes.add(humidity);
		attributes.add(new Attribute("windy", Boolean.TYPE));
		Attribute classAttribute = new Attribute("play", Boolean.TYPE);
		attributes.add(classAttribute);

		List<Instance> instances = new LinkedList<Instance>();

		// Weather data from the Weka sample data set weather.arff
		instances.add(new Instance(attributes, Arrays.asList("sunny", 	85, 85, false,	false)));
		instances.add(new Instance(attributes, Arrays.asList("sunny", 	80, 90, true,	false)));
		instances.add(new Instance(attributes, Arrays.asList("overcast",83, 86, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("rainy", 	70, 96, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("rainy", 	68, 80, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("rainy", 	65, 70, true,	false)));
		instances.add(new Instance(attributes, Arrays.asList("overcast",64, 65, true,	true)));
		instances.add(new Instance(attributes, Arrays.asList("sunny", 	72, 95, false,	false)));
		instances.add(new Instance(attributes, Arrays.asList("sunny", 	69, 70, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("rainy", 	75, 80, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("sunny", 	75, 70, true,	true)));
		instances.add(new Instance(attributes, Arrays.asList("overcast",72, 90, true,	true)));
		instances.add(new Instance(attributes, Arrays.asList("overcast",81, 75, false,	true)));
		instances.add(new Instance(attributes, Arrays.asList("rainy", 	71, 91, true,	false)));

		// Format input
		List<Rule> rulesTemp = new LinkedList<Rule>();
		rulesTemp.add(new Rule(temp, 70, "low", Rule.Comparator.LESS_THAN));
		rulesTemp.add(new Rule(temp, 80, "medium", Rule.Comparator.LESS_THAN));
		rulesTemp.add(new Rule(temp, 80, "medium", Rule.Comparator.EQUAL));
		rulesTemp.add(new Rule(temp, 80, "high", Rule.Comparator.GREATER_THAN));

		List<Rule> rulesHumidity = new LinkedList<Rule>();
		rulesHumidity.add(new Rule(humidity, 75, "low", Rule.Comparator.LESS_THAN));
		rulesHumidity.add(new Rule(humidity, 75, "low", Rule.Comparator.EQUAL));
		rulesHumidity.add(new Rule(humidity, 75, "high", Rule.Comparator.GREATER_THAN));

		List<Instance> modifiedInstances = new LinkedList<Instance>();
		for (Instance instance : instances) {
			boolean match = false;
			List<Object> values = new LinkedList<Object>(instance.values());
			for (Rule rule : rulesTemp) {
				if (rule.match(instance)) {
					int pos = attributes.indexOf(rule.attribute());
					values.set(pos, rule.classValue());
					match = true;
					break;
				}
			}
			for (Rule rule : rulesHumidity) {
				if (rule.match(instance)) {
					int pos = attributes.indexOf(rule.attribute());
					values.set(pos, rule.classValue());
					match = true;
					break;
				}
			}
			if (match) {
				modifiedInstances.add(new Instance(instance.attributes(), values));
			} else {
				modifiedInstances.add(instance);
			}
		}

		// Classify input
		OneR classifier = new OneR();
		classifier.learn(modifiedInstances, classAttribute);
	}

}