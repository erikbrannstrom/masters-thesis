import weka.core.Instances;
import weka.core.Attribute;
import weka.core.converters.ConverterUtils.DataSource;

public class DataDistribution
{
	private Instances data;
	private Attribute ctr;

	public DataDistribution(Instances data)
	{
		this.data = data;
		this.ctr = this.data.attribute("CTR");
	}

	public double mean()
	{
		return this.data.meanOrMode(this.ctr);
	}

	public double variance()
	{
		return this.data.variance(this.ctr);
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Missing command line arguments.");
			System.exit(1);
		}

		DataSource source = new DataSource(args[0]);
		DataDistribution dd = new DataDistribution(source.getDataSet());
		System.out.println(dd.mean() + " " + dd.variance());
	}

}