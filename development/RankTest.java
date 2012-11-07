import weka.core.*;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class RankTest
{
	Instances suggestions, validation;
	CampaignFactory factory;
	double stdError;

	public RankTest(CampaignFactory factory, Instances validation)
	{
		this.factory = factory;
		this.suggestions = factory.suggestions();
		this.validation = validation;
	}

	public RankTest(Instances suggestions, Instances validation)
	{
		this.suggestions = suggestions;
		this.validation = validation;
	}

	public int[] rankMatches()
	{
		int[] results = new int[this.suggestions.numInstances()];

		Instances realRanking = new Instances(this.validation, 0, 0);
		for (int i = 0; i < this.suggestions.numInstances(); i++) {
			Instance suggestion = this.suggestions.get(i);
			Instance real = this.findMatch(suggestion, this.validation);
			realRanking.add(real);
		}
		this.sortInstances(realRanking);

		this.sortInstances(this.suggestions);
		for (int i = 0; i < results.length; i++) {
			Instance suggestion = this.suggestions.get(i);
			Instance real = this.findMatch(suggestion, realRanking);
			//System.out.printf("Suggestion (%.2f) rank = %d, Real (%.2f) rank = %d\n", suggestion.classValue(), i, real.classValue(), realRanking.indexOf(real));

			if (realRanking.indexOf(real) == i) {
				results[i] = 1;
			} else {
				results[i] = 0;
			}
		}

		return results;
	}

	private void sortInstances(Instances instances)
	{
		Comparator<Instance> rateComparator = new Comparator<Instance>() {
			public int compare(Instance a, Instance b) {
				Attribute ctrMean = a.dataset().attribute("CTR");

				if (a.value(ctrMean) < b.value(ctrMean)) {
					return 1;
				} else if (a.value(ctrMean) > b.value(ctrMean)) {
					return -1;
				} else {
					return 0;
				}
			}
		};

		Collections.sort(instances, rateComparator);
	}

	private Instance findMatch(Instance orig, Instances instances)
	{
		for (Instance test : instances) {
			boolean match = true;
			for (int i = 0; i < instances.numAttributes(); i++) {
				if (instances.classIndex() == i) {
					continue;
				}
				if (Math.abs(orig.value(i) - test.value(i)) > 0.00001) {
					match = false;
					break;
				}
			}
			if (match) {
				return test;
			}
		}
		throw new RuntimeException("No matching instance for: " + orig.toString());
	}

}