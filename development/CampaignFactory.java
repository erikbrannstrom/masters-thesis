import weka.core.*;
import java.util.*;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;

public class CampaignFactory
{
	private Instances data, suggestions;
	private Classifier classifier;

	public CampaignFactory(Instances data)
	{
		this.data = data;
	}

	public Instances suggestions()
	{
		if (this.suggestions == null) {
			this.createSuggestions();
		}
		return this.suggestions;
	}

	public Classifier classifier()
	{
		if (this.classifier == null) {
			this.classifier = new SMOreg();
			try {
				this.classifier.buildClassifier(this.data);
			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}
		}
		return this.classifier;
	}

	private void createSuggestions()
	{
		// Create all combinations
		this.suggestions = new Instances(this.data, 0);
		DenseInstance inst = new DenseInstance(this.suggestions.numAttributes());
		inst.setDataset(this.suggestions);
		this.combineInstances(inst, 0);

		// Remove existing campaigns
		Iterator<Instance> it = this.suggestions.iterator();
		while (it.hasNext()) {
			Instance suggestion = it.next();
			for (Instance orig : this.data) {
				boolean remove = true;
				//System.out.println(orig + " vs. " + suggestion);
				for (int i = 0; i < this.data.numAttributes(); i++) {
					if (this.data.classIndex() == i) {
						//System.out.println(" - class");
						continue;
					}
					//System.out.println(" - " + Math.abs(suggestion.value(i) - orig.value(i)));
					if (Math.abs(suggestion.value(i) - orig.value(i)) > 0.0001) {
						remove = false;
						break;
					}
				}
				if (remove) {
					it.remove();
					break;
				}
			}
		}

		// Estimate action rates
		it = this.suggestions.iterator();
		while (it.hasNext()) {
			Instance suggestion = it.next();
			try {
				double est = this.classifier().classifyInstance(suggestion);
				suggestion.setClassValue(est);
			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}
		}
	}


	/*
	 * Copied from DataGenerator, probably should refactor this.
	 */
	private void combineInstances(Instance inst, int col)
	{
		if (col == this.data.numAttributes()-1) {
			this.suggestions.add(inst);
			return;
		} else if (this.data.classIndex() == col) {
			this.combineInstances(inst, col+1);
			return;
		}

		for (int i = 0; i < this.data.attribute(col).numValues(); i++) {
			DenseInstance newInst = new DenseInstance(inst);
			newInst.setDataset(inst.dataset());
			newInst.setValue(col, (double)i);
			this.combineInstances(newInst, col+1);
		}
	}

	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource(args[0]);
		Instances data = source.getDataSet();

		DataDistribution dd = new DataDistribution(data);
		DataGenerator gen = new DataGenerator(dd.mean(), dd.variance(), new Scenario1());

		data.setClassIndex(data.numAttributes()-1);

		double sumErrorRate = 0.0;
		double sumRandErrorRate = 0.0;
		int runs = 100;
		if (args.length > 1) {
			runs = Integer.parseInt(args[1]);
		}

		for (int i = 0; i < runs; i++) {
			DataGenerator gen2 = new DataGenerator(data);

			CampaignFactory cf = new CampaignFactory(gen2.trainingSet());

			SuggestionEvaluator suggestionEval = new SuggestionEvaluator(cf, data);
			sumErrorRate += suggestionEval.errorRatio();
			sumRandErrorRate += suggestionEval.randomErrorRatio();
		}

		System.out.printf("Average error rate over %d runs: %f\n", runs, sumErrorRate/runs);
		System.out.printf("Average error rate over %d runs for random values: %f\n", runs, sumRandErrorRate/runs);

		//suggestionEval.printEval();
	}

}