import java.util.*;

public class AnalysisModel implements Iterable<Pair<AnalysisModelEntry, Double>>
{
	public List<Pair<AnalysisModelEntry, Double>> model;

	public AnalysisModel()
	{
		this.model = new LinkedList<Pair<AnalysisModelEntry, Double>>();
	}

	public void add(AnalysisModelEntry rule, Double probability)
	{
		this.model.add(new Pair<AnalysisModelEntry, Double>(rule, probability));
	}

	public Iterator<Pair<AnalysisModelEntry, Double>> iterator()
	{
		return this.model.iterator();
	}

	public int size()
	{
		return this.model.size();
	}

	public Pair<AnalysisModelEntry, Double> get(int index)
	{
		return this.model.get(index);
	}

	public AnalysisModelEntry getEntry(int index)
	{
		return this.model.get(index).first();
	}

	public Double getSuccessRate(int index)
	{
		return this.model.get(index).second();
	}

	public Double getSuccessRate(AnalysisModelEntry entry)
	{
		for (Pair<AnalysisModelEntry, Double> pair : this.model) {
			if (pair.first().equals(entry)) {
				return pair.second();
			}
		}
		return new Double(0.0);
	}

	public void sort(Comparator<Pair<AnalysisModelEntry, Double>> comparator)
	{
		Collections.sort(this.model, comparator);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Rule (Success rate of classifier)\n---------------------------------\n");
		for (Pair<AnalysisModelEntry, Double> pair : this.model) {
			buffer.append(pair.first().toString());
			double err = Math.round(pair.second()*10000)/100.0;
			buffer.append(" (").append(err).append("%)\n");
		}
		buffer.delete(buffer.length()-1, buffer.length());

		return buffer.toString();
	}

}