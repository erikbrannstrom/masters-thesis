import java.util.*;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Capabilities.*;
import weka.core.*;
import weka.filters.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.core.converters.ConverterUtils.DataSource;

public class InstanceSimilarity
{
	private Instances data;

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Missing command line arguments.");
			System.exit(1);
		}
		String input = args[0];

		DataSource source = new DataSource(input);
		Instances data = source.getDataSet();

		// Remove useless attributes
		RemoveUseless filterUseless = new RemoveUseless();
		filterUseless.setInputFormat(data);
		data = Filter.useFilter(data, filterUseless);

		// Set class attribute if not already set, assumed to be the last
		if (data.classIndex() == -1) {
			data.setClassIndex(data.numAttributes() - 1);
		}

		// Test similarity of instances
		InstanceSimilarity sim = new InstanceSimilarity(data);
		Instance testInstance = new DenseInstance(data.firstInstance());
		testInstance.setDataset(data);

		// Test first instance, should return exact match
		System.out.println("Find closest match to the following instance:");
		System.out.println(testInstance);
		sim.getMostSimilar(testInstance);

		// Change gender to male, age = 0 (19-21), etc
		testInstance.setValue(0, 1);
		testInstance.setValue(1, 0);
		testInstance.setValue(2, 0);
		testInstance.setValue(3, 0);
		testInstance.setValue(4, 0);
		testInstance.setValue(5, 1);
		testInstance.setValue(7, 1);
		testInstance.setValue(8, 1);
		testInstance.setValue(10, 1);
		testInstance.setValue(11, 1);
		testInstance.setValue(12, 1);
		testInstance.setValue(13, 0);
		testInstance.setValue(15, 1);
		testInstance.setValue(16, 1);
		testInstance.setValue(17, 0);
		testInstance.setValue(18, 1);
		System.out.println("Find closest match to the following instance:");
		System.out.println(testInstance);
		sim.getMostSimilar(testInstance);
	}

	public InstanceSimilarity(Instances data)
	{
		this.data = data;
	}

	public Instance getMostSimilar(Instance inst)
	{
		double maxScore = 0;
		Instance bestMatch = null;
		for (int i = 0; i < this.data.numInstances(); i++) {
			double score = this.similarity(inst, this.data.get(i));
			if (score > maxScore) {
				bestMatch = this.data.get(i);
				maxScore = score;
			}
		}
		System.out.println(bestMatch.toString() + "\t\t" + maxScore);
		return bestMatch;
	}

	protected double similarity(Instance a, Instance b)
	{
		if (!a.equalHeaders(b)) {
			throw new RuntimeException("Instances do not have the same headers.");
		}
		double score = 0;
		for (int i = 0; i < a.numAttributes(); i++) {
			if (a.classIndex() == i) {
				continue;
			} else if (Math.abs(a.value(i)-b.value(i)) < 0.001) {
				score += 1.0;
			}
		}

		if (a.dataset().classIndex() < 0) {
			score = score / a.numAttributes();
		} else {
			score = score / (a.numAttributes()-1);
		}

		return score;
	}

}