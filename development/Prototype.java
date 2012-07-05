import java.util.*;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.rules.PART;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Prototype
{

	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.out.println("Argument for input file missing.");
			System.exit(-1);
		}

		// Read instances from input file
		Instances data = null;
		try {
			DataSource source = new DataSource(args[0]);
			data = source.getDataSet();
		} catch (Exception e) {
			System.out.println("Could not read from file " + args[0]);
			System.exit(-1);
		}

		// Create list of attribute objects that are not using Weka's class
		weka.core.Attribute wekaAttribute = null;
		List<Attribute> attributeList = new LinkedList<Attribute>();

		for (int i = 0; i < data.numAttributes(); i++) {
			wekaAttribute = data.attribute(i);
			if (wekaAttribute.type() == weka.core.Attribute.NOMINAL) {
				List<String> possibleValues = new LinkedList<String>();
				for (int j = 0; j < wekaAttribute.numValues(); j++) {
					possibleValues.add(wekaAttribute.value(j));
				}
				attributeList.add(new NominalAttribute(wekaAttribute.name(), possibleValues));
			} else {
				attributeList.add(new NumericAttribute(wekaAttribute.name(), 
					data.kthSmallestValue(wekaAttribute, 1), data.kthSmallestValue(wekaAttribute, data.numInstances())));
			}
		}

		// Set class attribute if not already set, assumed to be the last
		if (data.classIndex() == -1) {
		   data.setClassIndex(data.numAttributes() - 1);
		}

		// Create classifier
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		CostMatrix costMatrix = new CostMatrix(2);
		costMatrix.setElement(0, 1, 1.0);
		costMatrix.setElement(1, 0, 2500.0);
		csc.setCostMatrix(costMatrix);
		PART part = new PART();
		csc.setClassifier(part);

		// Build classifier using imported instances
		try {
			csc.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("Could not create classifier from instances.");
			System.exit(-1);
		}

		// Parse string of classification rules
		String[] rules = csc.toString().split("\n\n");
		AnalysisModel model = new AnalysisModel();

		for (String rule : rules) {
			String[] split = rule.split(":");
			if (split.length < 2) {
				continue;
			}
			String[] splitClass = split[1].trim().split(" ");
			if (splitClass[0].equalsIgnoreCase("yes")) {
				List<String> subRules = Arrays.asList(split[0].replaceAll("\n", " ").split(" AND "));
					AnalysisModelEntry entry = new AnalysisModelEntry();
					for (String subrule : subRules) {
						String[] ruleComponents = subrule.split(" ");
						Attribute att = new Attribute(ruleComponents[0]);
			
						Rule r = null;
						if (ruleComponents[2].matches("-?\\d+")) {
							r = new Rule(new Integer(ruleComponents[2]), Rule.Comparator.fromString(ruleComponents[1]));
						} else if (ruleComponents[2].matches("-?\\d+(.\\d+)?")) {
							r = new Rule(new Double(ruleComponents[2]), Rule.Comparator.fromString(ruleComponents[1]));
						} else {
							r = new Rule(ruleComponents[2], Rule.Comparator.fromString(ruleComponents[1]));
						}

						entry.add(new AttributeRule(att, r));
					}
				String[] measureSplit = splitClass[1].replaceAll("[()]", "").split("/");
				entry.removeRedundancy();
				model.add(entry, (1.0-Double.valueOf(measureSplit[1]) / Double.valueOf(measureSplit[0])));
			}
		}

		model.sort(new Comparator<Pair<AnalysisModelEntry, Double>>() {
			public int compare(Pair<AnalysisModelEntry, Double> o1, Pair<AnalysisModelEntry, Double> o2) {
				if (o1.equals(o2)) {
					return 0;
				} else if (o1.second() - o2.second() < 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		//System.out.println(model.toString());
		System.out.println("Available attributes:");
		for (Attribute ar : attributeList) {
			System.out.println(ar);
		}
		System.out.println("Enter one rule per line, finish with q + ENTER:");
		Scanner in = new Scanner(System.in);

		// Reads a single line from the console 
		// and stores into name variable
		List<AttributeRule> targetRules = new LinkedList<AttributeRule>();
		while (in.hasNextLine()) {
			String rule = in.nextLine();
			if (rule.equalsIgnoreCase("q")) {
				break;
			}
			String[] parsedRule = rule.split(" ");
			if (parsedRule.length != 3 || Rule.Comparator.fromString(parsedRule[1]) == null) {
				System.out.println("Incorrectly formated rule, exiting.");
				in.close();
				System.exit(-1);
			}

			Rule r = null;
			if (parsedRule[2].matches("-?\\d+")) {
				r = new Rule(new Integer(parsedRule[2]), Rule.Comparator.fromString(parsedRule[1]));
			} else if (parsedRule[2].matches("-?\\d+(.\\d+)?")) {
				r = new Rule(new Double(parsedRule[2]), Rule.Comparator.fromString(parsedRule[1]));
			} else {
				r = new Rule(parsedRule[2], Rule.Comparator.fromString(parsedRule[1]));
			}
			targetRules.add(new AttributeRule(new Attribute(parsedRule[0]), r));
		}

		in.close();

		System.out.println(targetRules);

		for (int i = 0; i < model.size(); i++) {
			AnalysisModelEntry entry = model.getEntry(i);
			if (entry.matchedBy(targetRules)) {
				double rate = Math.round(model.getSuccessRate(entry)*10000)/100.0;
				System.out.println(entry + " (" + rate + "%)");
			}
		}
	}

}