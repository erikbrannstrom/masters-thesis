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

		// Set class attribute if not already set
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
		List<List<String>> positiveRules = new LinkedList<List<String>>();
		List<Double> errorRates = new LinkedList<Double>();

		for (String rule : rules) {
			String[] split = rule.split(":");
			if (split.length < 2) {
				continue;
			}
			String[] splitClass = split[1].trim().split(" ");
			if (splitClass[0].equalsIgnoreCase("yes")) {
				List<String> subRules = Arrays.asList(split[0].replaceAll("\n", " ").split(" AND "));
				positiveRules.add(new LinkedList<String>(subRules));
				//positiveRules.get(positiveRules.size()-1).add("Click = " + splitClass[0]);
				String[] measureSplit = splitClass[1].replaceAll("[()]", "").split("/");
				errorRates.add(Double.valueOf(measureSplit[1]) / Double.valueOf(measureSplit[0]));
			}
		}

		String[] requirements = null;
		if (args.length > 1) {
			requirements = Arrays.copyOfRange(args, 1, args.length);
		}

		// Print in order of lowest error rate
		List<Double> sortedErrorRates = new LinkedList<Double>(errorRates);
		Collections.sort(sortedErrorRates);
		for (int i = 0; i < sortedErrorRates.size(); i++) {
			int index = errorRates.indexOf(sortedErrorRates.get(i));
			List<String> tmp = positiveRules.get(index);
			// Limit to those rules which fulfil the requirements specified in args, if any
			if (requirements != null && !tmp.containsAll(Arrays.asList(requirements))) {
				continue;
			}
			if (tmp.size() > 1) {
				double err = Math.round(errorRates.get(index)*10000)/100.0;
				System.out.print(tmp.toString() + " (" + err + "%)\n");
			}
		}

	}

}