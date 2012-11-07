import weka.core.*;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.Logistic;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

public class EstimationEvaluator
{
	Instances estimated, validation;
	double delta;

	public EstimationEvaluator(Instances estimated, Instances validation)
	{
		this.estimated = estimated;
		this.validation = validation;
	}

	public void delta(double d)
	{
		this.delta = d;
	}

	public double delta()
	{
		return this.delta;
	}

	public double successRate()
	{
		int count = 0;
		int errors = 0;

		for (Instance suggestion : this.estimated) {
			count++;

			double estimate = suggestion.classValue();
			Instance match = this.findMatch(suggestion, this.validation);

			double real = 0.0;
			if (match.classIndex() > 0) {
				real = match.classValue();
			} else {
				if (match.dataset().attribute("ActionRate") != null) {
					real = match.value(match.dataset().attribute("ActionRate"));
				} else if (match.dataset().attribute("Actions") != null
						&& match.dataset().attribute("Impressions") != null) {
					real = match.value(match.dataset().attribute("Actions")) / match.value(match.dataset().attribute("Impressions"));
				} else {
					throw new RuntimeException("No comparison action rate could be found.");
				}
			}

			if (real < estimate - this.delta() || real > estimate + this.delta()) {
				errors++;
			}
		}

		return 1.0-errors*1.0/count;
	}

	private Instance findMatch(Instance orig, Instances instances)
	{
		for (Instance test : instances) {
			boolean match = true;
			for (int i = 0; i < instances.numAttributes(); i++) {
				if (instances.attribute("Image").index() != i && instances.attribute("Text").index() != i) {
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
		return null;
	}

}