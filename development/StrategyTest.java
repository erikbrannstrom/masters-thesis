import weka.core.*;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class StrategyTest
{
	Instances suggestions, validation;
	CampaignFactory factory;
	double stdError;
	boolean suggestionsSorted;

	public StrategyTest(CampaignFactory factory, Instances validation)
	{
		this.factory = factory;
		this.suggestions = factory.suggestions();
		this.suggestionsSorted = false;
		this.validation = validation;
	}

	public StrategyTest(Instances suggestions, Instances validation)
	{
		this.suggestions = suggestions;
		this.suggestionsSorted = false;
		this.validation = validation;
	}

	public double[] errorRatio(int strategies, boolean weighted)
	{
		double[] results = new double[strategies];
		this.sortSuggestions();

		double sumReal = 0.0;
		double sumEst = 0.0;
		for (int i = 0; i < strategies; i++) {
			Instance suggestion = this.suggestions.get(i);
			Instance real = this.findMatch(suggestion, this.validation);

			if (weighted) {
				sumEst += suggestion.classValue();
				sumReal += suggestion.classValue()*real.classValue();
				results[i] = sumReal/sumEst;
			} else {
				sumReal += real.classValue();
				results[i] = sumReal/(i+1);
			}
		}

		return results;
	}

	private void sortSuggestions()
	{
		if (this.suggestionsSorted) {
			return;
		}
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

		Collections.sort(this.suggestions, rateComparator);
		this.suggestionsSorted = true;
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

	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource(args[0]);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		source = new DataSource(args[1]);
		Instances suggestions = source.getDataSet();
		suggestions.setClassIndex(suggestions.numAttributes() - 1);

		StrategyTest strat = new StrategyTest(suggestions, data);
		int strategies = 3;
		double[] res = strat.errorRatio(strategies, true);
		for (int i = 0; i < strategies; i++) {
			System.out.printf("%d: %f\n", i+1, res[i]);
		}
	}

}