import weka.core.*;
import weka.classifiers.*;

public class NumericEstimator extends Estimator
{
	private Classifier classifier;

	public NumericEstimator(Instances knowledge, Classifier classifier)
	{
		super(knowledge);
		this.classifier(classifier);
	}

	/**
	 * Converts the Actions and Impressions to a single ActionRate attribute.
	 */
	protected void knowledge(Instances knowledge)
	{
		if (knowledge.attribute("ActionRate") != null) {
			this.knowledge = knowledge;
			return;
		}

		// Add action rate
		knowledge.insertAttributeAt(new Attribute("Click Rate"), knowledge.numAttributes());
		Attribute ar = knowledge.attribute("Click Rate");
		Attribute actions = knowledge.attribute("Clicks Count");
		Attribute impressions = knowledge.attribute("Impressions");

		if (actions == null || impressions == null) {
			throw new RuntimeException("The required attributes could not be found in data set.");
		}

		for (int i = 0; i < knowledge.numInstances(); i++) {
			Instance inst = knowledge.get(i);
			double val = inst.value(actions)/inst.value(impressions);
			inst.setValue(ar, val);
		}

		knowledge.setClass(ar);
		knowledge.deleteAttributeAt(impressions.index());
		knowledge.deleteAttributeAt(actions.index());

		this.knowledge = knowledge;
	}

	private void classifier(Classifier classifier)
	{
		this.classifier = classifier;
		try {
			this.classifier.buildClassifier(this.knowledge);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public double estimate(Instance instance)
	{
		try {
			return this.classifier.classifyInstance(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}