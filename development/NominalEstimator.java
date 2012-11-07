import weka.core.*;
import weka.classifiers.*;
import java.util.*;

public class NominalEstimator extends Estimator
{
	private Classifier classifier;

	public NominalEstimator(Instances knowledge, Classifier classifier)
	{
		super(knowledge);
		this.classifier(classifier);
	}

	/**
	 * Converts each instance into two separate weighted instances, one for action taken and one for action not taken.
	 */
	protected void knowledge(Instances knowledge)
	{
		if (knowledge.attribute("Click Rate") != null) {
			this.knowledge = knowledge;
			return;
		}

		Attribute actions = knowledge.attribute("Clicks_Count");
		Attribute impressions = knowledge.attribute("Impressions");

		if (actions == null || impressions == null) {
			throw new RuntimeException("The required attributes could not be found in data set.");
		}

		// Add clicked attribute
		List<String> yesNo = new LinkedList<String>();
		yesNo.add("yes");
		yesNo.add("no");
		knowledge.insertAttributeAt(new Attribute("Action", yesNo), knowledge.numAttributes());
		Attribute action = knowledge.attribute("Action");

		int length = knowledge.numInstances();
		for (int i = 0; i < length; i++) {
			Instance yes = knowledge.instance(i);
			Instance no = (Instance)yes.copy();
			yes.setValue(action.index(), "yes");
			no.setValue(action.index(), "no");
			yes.setWeight(1.0*yes.value(actions)+1);
			no.setWeight(1.0*yes.value(impressions)-yes.value(actions)+1);
			knowledge.add(no);
		}

		knowledge.setClass(action);
		knowledge.deleteAttributeAt(impressions.index());
		knowledge.deleteAttributeAt(actions.index());

		this.knowledge = knowledge;
		System.out.println(knowledge);
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
			double[] distr = this.classifier.distributionForInstance(instance);
			return distr[this.knowledge.classAttribute().indexOfValue("yes")];
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}