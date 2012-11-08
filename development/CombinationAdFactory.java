import java.util.*;
import weka.core.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.Remove;

public class CombinationAdFactory implements AdFactory
{
	private DataManager dataManager;
	private Instances data, suggestions;

	public CombinationAdFactory(DataManager dataManager)
	{
		this.dataManager = dataManager;
		this.data = this.dataManager.get();
	}

	public Ads all()
	{
		if (this.suggestions == null) {
			this.createSuggestions();
		}
		return new Ads(this.suggestions);
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
					if ( ! Ads.AD.contains(this.data.attribute(i).name().replaceAll("_", " ")) ) {
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

		// Find all attributes that are campaign properties (target, ad or metrics)
		List<Integer> keepIndices = new LinkedList<Integer>();
		for (int i = 0; i < this.suggestions.numAttributes(); i++) {
			String attrName = this.suggestions.attribute(i).name();
			if ( Ads.AD.contains(attrName.replaceAll("_", " ")) ) {
				keepIndices.add(i+1); // String based indices start at 1, not 0
			}
		}

		// Keep only attributes that have been identified as campaign properties
		try {
			Remove removeFilter = new Remove();
			removeFilter.setAttributeIndices(keepIndices.toString().replaceAll("[^0-9,]", ""));
			removeFilter.setInvertSelection(true);
			removeFilter.setInputFormat(this.suggestions);

			// Filter instances
			this.suggestions = Filter.useFilter(this.suggestions, removeFilter);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
		} else if (!Ads.AD.contains(this.data.attribute(col).name().replaceAll("_", " "))) {
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

}