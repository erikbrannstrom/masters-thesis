import weka.core.*;
import java.util.*;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMOreg;

public class CampaignFactoryCI
{
	private Instances data, suggestions;
	private Classifier classifier;

	public CampaignFactoryCI(Instances data)
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
				for (int i = 0; i < this.data.numAttributes(); i++) {
					if (this.data.classIndex() == i) {
						continue;
					}
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

		// Create models
		Instances dataNoDelta = new Instances(this.data);
		dataNoDelta.setClassIndex(this.data.attribute("CTR-Mean").index());
		dataNoDelta.deleteAttributeAt(this.data.attribute("CTR-Delta").index());
		Instances dataNoMean = new Instances(this.data);
		dataNoMean.setClassIndex(this.data.attribute("CTR-Delta").index());
		dataNoMean.deleteAttributeAt(this.data.attribute("CTR-Mean").index());
		Classifier meanClassifier = new SMOreg();
		Classifier deltaClassifier = new SMOreg();
		try {
			meanClassifier.buildClassifier(dataNoDelta);
			deltaClassifier.buildClassifier(dataNoMean);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		// Estimate action rates
		it = this.suggestions.iterator();
		while (it.hasNext()) {
			Instance suggestion = it.next();
			try {
				Instance meanInstance = new DenseInstance(suggestion);
				meanInstance.deleteAttributeAt(this.data.attribute("CTR-Delta").index());
				meanInstance.setDataset(dataNoDelta);
				double mean = meanClassifier.classifyInstance(meanInstance);

				Instance deltaInstance = new DenseInstance(suggestion);
				deltaInstance.deleteAttributeAt(this.data.attribute("CTR-Mean").index());
				deltaInstance.setDataset(dataNoMean);
				double delta = deltaClassifier.classifyInstance(deltaInstance);

				suggestion.setValue(this.suggestions.attribute("CTR-Mean"), mean);
				suggestion.setValue(this.suggestions.attribute("CTR-Delta"), delta);
			} catch (Exception e) {
				throw new RuntimeException(e);
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
		} else if (col == this.data.attribute("CTR-Mean").index() || col == this.data.attribute("CTR-Delta").index()) {
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

		data.setClassIndex(data.numAttributes()-1);

		double sumErrorRate = 0.0;
		int runs = 1000;
		for (int i = 0; i < runs; i++) {
			DataGenerator gen = new DataGenerator(data);
			CampaignFactoryCI cf = new CampaignFactoryCI(gen.trainingSet());

			SuggestionEvaluatorCI suggestionEval = new SuggestionEvaluatorCI(cf, data);
			sumErrorRate += suggestionEval.errorRatio();
		}

		System.out.printf("Average error rate over %d runs: %f\n", runs, sumErrorRate/runs);

		/*DataGenerator gen = new DataGenerator(data);
		CampaignFactoryCI cf = new CampaignFactoryCI(gen.trainingSet());
		System.out.println(cf.suggestions());*/
	}

}