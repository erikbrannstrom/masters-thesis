import java.util.*;
import java.math.BigDecimal;
import weka.core.Attribute;
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
		if (args.length < 2) {
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

		Instance targetInstance = new DenseInstance(data.numAttributes());
		targetInstance.setDataset(data);
		String[] targets = args[1].split(",");
		for (String target : targets) {
			String[] attrValue = target.split("=");
			Attribute attr = data.attribute(attrValue[0]);
			if (attr.isNominal()) {
				targetInstance.setValue(attr, attrValue[1]);
			} else {
				targetInstance.setValue(attr, Double.parseDouble(attrValue[1]));
			}
		}

		System.out.println("Find closest match to the following instance:");
		System.out.println(targetInstance);
		System.out.println();
		sim.sortByEstimate(targetInstance);

		Attribute ctrLow = data.attribute("CTR-Low");

		for (Instance match : data) {
			double ctrMin = sim.similarity(targetInstance, match) * match.value(ctrLow);
			if (ctrMin < 0.0) {
				ctrMin = 0.0;
			} else {
				ctrMin *= 100.0;
			}
			System.out.printf("%-120s CTR%% >= %.5f%%\n", match, BigDecimal.valueOf(ctrMin));
		}
	}

	public InstanceSimilarity(Instances data)
	{
		this.data = data;
	}

	public void sortBySimilarity(final Instance inst)
	{
		Collections.sort(this.data, new Comparator<Instance>() {
			public int compare(Instance i1, Instance i2) {
				double simScore1 = InstanceSimilarity.this.similarity(inst, i1);
				double simScore2 = InstanceSimilarity.this.similarity(inst, i2);
				if (Math.abs(simScore1-simScore2) < 0.00001) {
					return 0;
				} else if (simScore1 < simScore2) {
					return -1;
				} else {
					return 1;
				}
			}
		});
	}

	public void sortByEstimate(final Instance inst)
	{
		final Attribute ctrLow = this.data.attribute("CTR-Low");
		Collections.sort(this.data, new Comparator<Instance>() {
			public int compare(Instance i1, Instance i2) {
				double simScore1 = InstanceSimilarity.this.similarity(inst, i1) * i1.value(ctrLow);
				double simScore2 = InstanceSimilarity.this.similarity(inst, i2) * i2.value(ctrLow);
				if (Math.abs(simScore1-simScore2) < 0.0000001) {
					return 0;
				} else if (simScore1 < simScore2) {
					return -1;
				} else {
					return 1;
				}
			}
		});
	}

	protected double similarity(Instance a, Instance b)
	{
		if (!a.equalHeaders(b)) {
			throw new RuntimeException("Instances do not have the same headers.");
		}
		double score = 0;
		int numCountedAttributes = 0;
		for (int i = 0; i < a.numAttributes(); i++) {
			if (a.classIndex() == i || a.isMissing(i) || b.isMissing(i)) {
				continue;
			} else if (Math.abs(a.value(i)-b.value(i)) < 0.001) {
				score += 1.0;
			}
			numCountedAttributes++;
		}

		return score / numCountedAttributes;
	}

}
