import java.util.*;
import weka.core.*;

public class Scenario1 implements Scenario
{
	private List<String> headers;
	private List<List<Double>> data;

	public Scenario1()
	{
		headers = new ArrayList<String>();
		data = new ArrayList<List<Double>>();

		// Set headers
		headers.add("Image");
		headers.add("Text");

		// Image weights
		List<Double> column = new LinkedList<Double>();
		column.add(2.5);
		column.add(2.0);
		column.add(1.5);
		column.add(1.0);
		data.add(column);

		// Text weights
		column = new LinkedList<Double>();
		column.add(1.0);
		column.add(0.5);
		column.add(0.25);
		data.add(column);
	}

	public String header(int col)
	{
		return headers.get(col);
	}

	public List<Double> data(int col)
	{
		return data.get(col);
	}

	public int columns()
	{
		return this.headers.size();
	}

}