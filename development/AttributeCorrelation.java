import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.Utils;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.Filter;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;

public class AttributeCorrelation
{
	private Instances data;

	public AttributeCorrelation(Instances data)
	{
		this.data = data;
	}

	public double correlation(String a, String b)
	{
		return this.correlation(this.data.attribute(a), this.data.attribute(b));
	}

	public double correlation(Attribute a, Attribute b)
	{
		double[] aValues = new double[data.numInstances()];
		double[] bValues = new double[data.numInstances()];

		for (int i = 0; i < data.numInstances(); i++) {
			Instance inst = data.get(i);
			aValues[i] = inst.value(a);
			bValues[i] = inst.value(b);
			if (Double.isNaN(aValues[i])) {
				aValues[i] = 0;
			}
			if (Double.isNaN(bValues[i])) {
				bValues[i] = 0;
			}
		}

		return Utils.correlation(aValues, bValues, data.numInstances());
	}

	public void printCorrelationMatrix()
	{
		for (int i = 0; i < this.data.numAttributes(); i++) {
			Attribute attr1 = this.data.attribute(i);
			System.out.printf("%20s \t", attr1.name());
			for (int j = 0; j < this.data.numAttributes(); j++) {
				Attribute attr2 = this.data.attribute(j);
				if (attr1.equals(attr2)) {
					System.out.print("- \t");
					continue;
				}
				double res = this.correlation(attr1, attr2);
				res = Math.round(res*1000)/1000.0;
				System.out.print(res + "\t");
			}
			System.out.print("\n");
		}
	}

	public static void main(String[] args) throws Exception {
		String input = args[0];

		DataSource source = new DataSource(input);
		Instances data = source.getDataSet();

		// Remove useless attributes
		int numAttributesOriginal = data.numAttributes();
		RemoveUseless filterUseless = new RemoveUseless();
		filterUseless.setInputFormat(data);
		data = Filter.useFilter(data, filterUseless);

		AttributeCorrelation corr = new AttributeCorrelation(data);
		corr.printCorrelationMatrix();

	}

}