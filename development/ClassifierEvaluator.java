import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class ClassifierEvaluator
{

	public static int evaluate(Classifier classifier, Instances testData)
	{
		int classIndex = testData.classIndex();
		int correctClassifications = 0;
		for (int i = 0; i < testData.numInstances(); i++) {
			Instance inst = testData.get(i);
			double classValue = inst.classValue();
			inst.setMissing(classIndex);
			try {
				if (classValue == classifier.classifyInstance(inst)) {
					correctClassifications += (int)inst.weight();
				}
			} catch (Exception e) {

			}
		}
		return correctClassifications;
	}

}