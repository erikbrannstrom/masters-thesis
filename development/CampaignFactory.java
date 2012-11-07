import weka.core.*;
import java.util.*;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.*;
import weka.classifiers.trees.M5P;

public class CampaignFactory
{
	private Instances data, suggestions;
	private Classifier classifier;

	public CampaignFactory(Instances data)
	{
		this.data = data;
	}

	public Instances data()
	{
		return this.data;
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
			this.setClassifier(new SMOreg());
		}
		return this.classifier;
	}

	public void setClassifier(Classifier classifier)
	{
		this.classifier = classifier;
		try {
			this.classifier.buildClassifier(this.data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

		data.setClassIndex(data.numAttributes()-1);

		int runs = 1000;
		if (args.length > 1) {
			runs = Integer.parseInt(args[1]);
		}

		List<Double> ks = new LinkedList<Double>();
		ks.add(0.01);
		ks.add(0.05);
		ks.add(0.10);
		ks.add(0.20);

		for (double k : ks) {
			System.out.println("k = " + k);
			String[] opts = { "-R", "1000" };
			experiment(data, runs, k, 0.10, "weka.classifiers.functions.Logistic", opts);
		}

/*
		for (double k : ks) {
			for (double ratio = 0.05; ratio < 0.95; ratio += 0.05) {
				System.out.printf("[Experiment: k=%.2f, ratio=%.2f]\n", k, ratio);
				experiment(data, runs, k, ratio, "weka.classifiers.functions.SMOreg");
			}
		}
*/
/*
		for (double k : ks) {
			System.out.println("k = " + k);
			experiment(data, runs, k, 0.10, "weka.classifiers.functions.SMOreg");
			experiment(data, runs, k, 0.10, "weka.classifiers.functions.LinearRegression");
			experiment(data, runs, k, 0.10, "weka.classifiers.trees.M5P");
			experiment(data, runs, k, 0.10, "weka.classifiers.lazy.IBk");
			experiment(data, runs, k, 0.10, "weka.classifiers.functions.MultilayerPerceptron");
			experiment(data, runs, k, 0.10, "weka.classifiers.trees.REPTree");
		}
*/

	}

	private static void experiment(Instances data, int runs, double k, double validationRatio, String classifierClass)
	{
		experiment(data, runs, k, validationRatio, classifierClass, null);
	}

	private static void experiment(Instances data, int runs, double k, double validationRatio, String classifierClass, String[] opts)
	{
/*
		int strategies = 3;
		double[] avgRates = new double[strategies];
		double[] avgRatesWeighted = new double[strategies];
		double deltaSum = 0.0;

		// Constant!
		double avgRateWomen = 0.000237927;
		double avgRateMen = 0.00021608;

		double avgRate = avgRateWomen;

		double sumErrorRate = 0.0;
		double sumRandErrorRate = 0.0;
		double sumUniformMinMaxErrorRate = 0.0;

		List<Double> rankRates = new LinkedList<Double>();

		for (int i = 0; i < runs; i++) {
			if (i % 200 == 0 && i != 0) {
				double comp = 100*i/runs;
				//System.out.printf("%.2f%% complete..\n", comp);
			}
			DataGenerator gen2 = new DataGenerator(data);
			gen2.setValidationRatio(validationRatio);

			CampaignFactory cf = new CampaignFactory(gen2.trainingSet());
			try {
				cf.setClassifier(AbstractClassifier.forName(classifierClass, opts));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			SuggestionEvaluator suggestionEval = new SuggestionEvaluator(cf, data);
			suggestionEval.delta(avgRate*k);
			sumErrorRate += suggestionEval.errorRatio();
			//sumRandErrorRate += suggestionEval.randomErrorRatio();
			//sumUniformMinMaxErrorRate += suggestionEval.uniformMinMaxErrorRatio();
			deltaSum += suggestionEval.delta();


			RankTest rank = new RankTest(cf, data);
			int[] res = rank.rankMatches();
			for (int rankIndex = 0; rankIndex < res.length; rankIndex++) {
				if (rankRates.size() > rankIndex) {
					rankRates.set(rankIndex, rankRates.get(rankIndex)+res[rankIndex]);
				} else {
					rankRates.add(rankIndex, (double)res[rankIndex]);
				}
			}

			StrategyTest strat = new StrategyTest(cf, data);
			double[] res = strat.errorRatio(strategies, false);
			for (int j = 0; j < strategies; j++) {
				avgRates[j] += res[j];
			}

			res = strat.errorRatio(strategies, true);
			for (int j = 0; j < strategies; j++) {
				avgRatesWeighted[j] += res[j];
			}

		}

		for (int j = 0; j < rankRates.size(); j++) {
			System.out.printf("Average success rate for rank %d: %f\n", (j+1), rankRates.get(j)/runs);
		}

		for (int j = 0; j < strategies; j++) {
			System.out.printf("Average action rate for Strategy %d over %d runs: %f\n", (j+1), runs, avgRates[j]/runs);
		}
		for (int j = 0; j < strategies; j++) {
			System.out.printf("Average action rate for weighted Strategy %d over %d runs: %f\n", (j+1), runs, avgRatesWeighted[j]/runs);
		}
*/
		//System.out.printf("%s: %f\n", classifierClass, 1-sumErrorRate/runs);
		//System.out.printf("Success rate over %d runs for normal random values: %f\n", runs, 1-sumRandErrorRate/runs);
		//System.out.printf("Success rate over %d runs for uniform random values with min/max: %f\n", runs, 1-sumUniformMinMaxErrorRate/runs);

		//System.out.printf("Average delta: %f\n", deltaSum/runs);
		//System.out.printf("Average click rate for women in data set: %f\n", avgRateWomen);

		//suggestionEval.printEval();
	}

}