import weka.core.*;
import java.util.*;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.*;

public abstract class Estimator
{
	protected Instances knowledge;

	/**
	 * Constructor for abstract estimator class.
	 *
	 * The knowledge instances are expected to have a Clicks attribute and an Impressions attribute.
	 */
	protected Estimator(Instances knowledge)
	{
		this.knowledge(knowledge);
	}

	public static Estimator factory(Instances knowledge, String className, String[] options)
	{
		try {
			AbstractClassifier classifier = (AbstractClassifier)AbstractClassifier.forName(className, options);
			if (classifier.getCapabilities().handles(Capabilities.Capability.NUMERIC_CLASS)) {
				return new NumericEstimator(knowledge, classifier);
			} else {
				return new NominalEstimator(knowledge, classifier);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void knowledge(Instances knowledge);
	public abstract double estimate(Instance instance);

}