import weka.core.*;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;

public class SuggestionEvaluatorCI
{
	Instances suggestions, validation;
	CampaignFactoryCI factory;
	double stdError;

	public SuggestionEvaluatorCI(CampaignFactoryCI factory, Instances validation)
	{
		this.factory = factory;
		this.suggestions = factory.suggestions();
		this.validation = validation;
	}

	public double errorRatio()
	{
		try {
			int count = 0;
			int errors = 0;

			for (Instance suggestion : this.suggestions) {
				count++;
				Instance match = this.findMatch(suggestion, this.validation);

				double delta = suggestion.value(this.suggestions.attribute("CTR-Delta"));
				double suggestedMean = suggestion.value(this.suggestions.attribute("CTR-Mean"));
				double actualMean = match.value(this.validation.attribute("CTR-Mean"));
				if (actualMean > suggestedMean-delta && actualMean < suggestedMean+delta) {

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
				if (i == instances.attribute("CTR-Mean").index() || i == instances.attribute("CTR-Delta").index()) {
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