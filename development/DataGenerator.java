import weka.core.*;
import java.util.*;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class DataGenerator
{
	private double validationRatio = 0.10;
	private int seedRate;
	private int seedSplit;
	private double mean, variance;
	private Scenario scenario;
	private Instances data, training, validation;

	public DataGenerator(double mean, double variance, Scenario scenario)
	{
		this();
		this.mean = mean;
		this.variance = variance;
		this.scenario = scenario;
	}

	public DataGenerator(Instances data)
	{
		this();
		this.data = data;
	}

	private DataGenerator()
	{
		this.seedRate = (int)Math.round(Math.random()*Calendar.getInstance().getTimeInMillis()) % Integer.MAX_VALUE;
		this.seedSplit = (int)Math.round(Math.random()*Calendar.getInstance().getTimeInMillis()) % Integer.MAX_VALUE;
	}

	public void setSeedRate(int seed)
	{
		this.seedRate = seed;
	}

	public void setSeedSplit(int seed)
	{
		this.seedSplit = seed;
	}

	public Instances trainingSet()
	{
		if (this.training == null) {
			this.splitSets();
		}

		return this.training;
	}

	public Instances validationSet()
	{
		if (this.validation == null) {
			this.splitSets();
		}

		return this.validation;
	}

	public Instances data()
	{
		if (this.data == null) {
			this.createData();
		}
		return this.data;
	}

	private void createData()
	{
		int combinations = 1;
		// Create header
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < this.scenario.columns(); i++) {
			List<String> values = new LinkedList<String>();
			if (this.scenario.data(i).size() > 0) {
				combinations *= this.scenario.data(i).size();
			}
			for (int j = 0; j < this.scenario.data(i).size(); j++) {
				values.add(this.scenario.header(i) + " " + (j+1));
			}
			attributes.add(new Attribute(this.scenario.header(i), values));
		}
		Attribute ar = new Attribute("ActionRate");
		attributes.add(ar);

		// Initialize data set
		this.data = new Instances("Generated Data", attributes, combinations);
		this.data.setClass(ar);

		// Create instances with weighting as action rate
		Instance inst = new DenseInstance(this.data.numAttributes());
		inst.setDataset(this.data);
		inst.setClassValue(1.0);
		this.combineInstances(inst, 0);

		// Draw as many values as there are instances from normal action rate distribution
		List<Double> rates = new LinkedList<Double>();
		Normal distr = new Normal(this.mean, Math.sqrt(this.variance), new MersenneTwister(this.seedRate));
		for (int i = 0; i < this.data.numInstances(); i++) {
			rates.add(distr.nextDouble());
		}
		Collections.sort(rates);

		// Sort instances based on action rate (weighting)
		Collections.sort(this.data, new Comparator<Instance>() {
			public int compare(Instance a, Instance b) {
				if (a.classValue() < b.classValue()) {
					return -1;
				} else if (a.classValue() > b.classValue()) {
					return 1;
				}
				return 0;
			}
		});

		// Assign action rates
		for (int i = 0; i < this.data.numInstances(); i++) {
			this.data.get(i).setClassValue(rates.get(i));
		}
	}

	private void combineInstances(Instance inst, int col)
	{
		if (col == this.scenario.columns()) {
			this.data.add(inst);
			return;
		}

		for (int i = 0; i < this.scenario.data(col).size(); i++) {
			DenseInstance newInst = new DenseInstance(inst);
			newInst.setDataset(inst.dataset());
			newInst.setValue(col, (double)i);
			newInst.setClassValue(newInst.classValue()*this.scenario.data(col).get(i));
			this.combineInstances(newInst, col+1);
		}
	}

	private void splitSets()
	{
		this.training = new Instances(this.data());
		this.validation = new Instances(this.data(), 0, 0);

		MersenneTwister rnd = new MersenneTwister(this.seedSplit);
		int validationSize = (int)Math.round(Math.ceil(this.training.numInstances()*this.validationRatio));
		for (int i = 0; i < validationSize; i++) {
			int index = Math.abs(rnd.nextInt()) % this.training.numInstances();
			Instance inst = this.training.get(index);
			inst.setDataset(this.validation);
			this.validation.add(inst);
			this.training.remove(index);
		}
	}

	public static void main(String[] args) {
		DataGenerator gen = new DataGenerator(Double.valueOf(args[0]), Double.valueOf(args[1]), new Scenario1());
		System.out.println("Complete data set");
		System.out.println(gen.data());
		System.out.println();
		System.out.println("Training set");
		System.out.println(gen.trainingSet());
		System.out.println();
		System.out.println("Validation set");
		System.out.println(gen.validationSet());
	}

}