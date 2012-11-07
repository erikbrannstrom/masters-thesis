import weka.core.*;
import java.util.*;
import weka.classifiers.*;
import weka.classifiers.functions.*;
import weka.core.converters.ConverterUtils.DataSource;

public class DataSplitter
{
	private Instances original, split, remaining;
	private double ratio;

	/**
	 * A DataSplitter will split the input data into two non-overlapping sets by randomly moving instances from the
	 * original set to a new set. The number of instances to be moved is specified by the ratio.
	 *
	 * @param data Data set to be split
	 * @param ratio Real number between 0 and 1 (inclusive)
	 */
	public DataSplitter(Instances data, double ratio)
	{
		if (ratio < 0 || ratio > 1) {
			throw new RuntimeException("Ratio must be a value between 0 and 1.");
		}

		this.original = data;
		this.ratio = ratio;
	}

	public Instances remaining()
	{
		if (this.split == null) {
			this.performSplit();
		}
		return this.remaining;
	}

	public Instances split()
	{
		if (this.split == null) {
			this.performSplit();
		}
		return this.split;
	}

	private void performSplit()
	{
		this.remaining = new Instances(this.original);
		this.split = new Instances(this.original, 0, 0);

		Random rnd = new Random();
		int validationSize = (int)Math.round(Math.ceil(this.remaining.numInstances()*this.ratio));
		for (int i = 0; i < validationSize; i++) {
			int index = Math.abs(rnd.nextInt()) % this.remaining.numInstances();
			Instance inst = this.remaining.get(index);
			inst.setDataset(this.split);
			this.split.add(inst);
			this.remaining.remove(index);
		}
	}

}