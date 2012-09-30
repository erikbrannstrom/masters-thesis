import weka.core.*;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class SuggestionEvaluator
{
	Instances suggestions, validation;
	CampaignFactory factory;
	double stdError;

	public SuggestionEvaluator(CampaignFactory factory, Instances validation)
	{
		this.factory = factory;
		this.suggestions = factory.suggestions();
		this.validation = validation;
	}

	public void printEval()
	{
		try {
			Evaluation eval = new Evaluation(this.validation);
			eval.crossValidateModel(this.factory.classifier(), this.validation, 10, new Random());

			double meanError = eval.meanAbsoluteError();

			for (Instance suggestion : this.suggestions) {
				Instance match = this.findMatch(suggestion, this.validation);
				System.out.println(suggestion);

				double diff = suggestion.classValue() - match.classValue();
				double diffPercentage = Math.round(10000*diff / match.classValue())/100.0;
				/*if (match.classValue() == 0) {
					System.out.println(" - " + diff + " (Inf)");
				} else {
					System.out.printf(" - %f (%f%%)\n", diff, diffPercentage);
				}*/
				if (match.classValue() > suggestion.classValue() - meanError && match.classValue() < suggestion.classValue() + meanError) {
					System.out.println(" - Inside mean error limits");
				} else {
					System.out.printf(" - (!) Outside mean error limits, real value: %f\n", match.classValue());
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	public double errorRatio()
	{
		try {
			Evaluation eval = new Evaluation(this.validation);
			eval.crossValidateModel(this.factory.classifier(), this.validation, 10, new Random(131974));

			double meanError = eval.meanAbsoluteError();
			int count = 0;
			int errors = 0;

			for (Instance suggestion : this.suggestions) {
				count++;
				Instance match = this.findMatch(suggestion, this.validation);

				double diff = suggestion.classValue() - match.classValue();
				double diffPercentage = Math.round(10000*diff / match.classValue())/100.0;
				if (match.classValue() > suggestion.classValue() - meanError && match.classValue() < suggestion.classValue() + meanError) {

				} else {
					errors++;
				}
			}

			return errors*1.0/count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public double randomErrorRatio()
	{
		DataDistribution dd = new DataDistribution(this.validation);
		try {
			Evaluation eval = new Evaluation(this.validation);
			eval.crossValidateModel(this.factory.classifier(), this.validation, 10, new Random(131974));

			double meanError = eval.meanAbsoluteError();
			int count = 0;
			int errors = 0;

			for (Instance suggestion : this.suggestions) {
				count++;
				Instance match = this.findMatch(suggestion, this.validation);

				Normal distr = new Normal(dd.mean(), Math.sqrt(dd.variance()), new MersenneTwister((int)(Math.random()*Integer.MAX_VALUE)));
				double est = distr.nextDouble();
				if (match.classValue() > est - meanError && match.classValue() < est + meanError) {

				} else {
					errors++;
				}
			}

			return errors*1.0/count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private Instance findMatch(Instance orig, Instances instances)
	{
		for (Instance test : instances) {
			boolean match = true;
			for (int i = 0; i < instances.numAttributes(); i++) {
				if (instances.classIndex() == i) {
					continue;
				}
				if (Math.abs(orig.value(i) - test.value(i)) > 0.0001) {
					match = false;
					break;
				}
			}
			if (match) {
				return test;
			}
		}
		return null;
	}

}