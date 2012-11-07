import java.util.*;
import weka.core.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.DatabaseSaver;
import java.io.*;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.nio.charset.Charset;

public class DuegoDataParser
{

	public static Instances parse(File file)
	{
		try {
			// Because the reports from Facebook are UTF16, tab-delimited and drops missing values at end of line
			// we need to make the data interpretable by Weka
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF16"), '\t');
			List<String[]> allElements = reader.readAll();

			// If the CSV file has dropped empty values at the end of a line, we need to explicitly add those fields
			int numColumns = allElements.get(0).length;
			for (int i = 1; i < allElements.size(); i++) {
				if (allElements.get(i).length < numColumns) {
					String[] longerRow = new String[numColumns];
					System.arraycopy(allElements.get(i), 0, longerRow, 0, allElements.get(i).length);
					for (int j = allElements.get(i).length; j < numColumns; j++) {
						longerRow[j] = "";
					}
					allElements.set(i, longerRow);
				}
			}

			// Write the new CSV to a temporary file using UTF8
			File tmp = File.createTempFile("adEst", ".csv");
			Writer fileWriter = new OutputStreamWriter(new FileOutputStream(tmp), "UTF8");
			CSVWriter writer = new CSVWriter(fileWriter, ',');
			writer.writeAll(allElements);
			writer.close();

			// Now let Weka read the temporary file, then delete it
			DataSource source = new DataSource(tmp.getAbsolutePath());
			Instances inst = source.getDataSet();
			tmp.delete();

			// Find all attributes that are campaign properties (target, ad or metrics)
			List<Integer> keepIndices = new LinkedList<Integer>();
			for (int i = 0; i < inst.numAttributes(); i++) {
				String attrName = inst.attribute(i).name();
				if ( Ads.TARGETS.contains(attrName) || Ads.METRICS.contains(attrName) || Ads.AD.contains(attrName) ) {
					keepIndices.add(i+1); // String based indices start at 1, not 0
				}
			}

			// Keep only attributes that have been identified as campaign properties
			Remove removeFilter = new Remove();
			removeFilter.setAttributeIndices(keepIndices.toString().replaceAll("[^0-9,]", ""));
			removeFilter.setInvertSelection(true);
			removeFilter.setInputFormat(inst);

			// Return instances
			return Filter.useFilter(inst, removeFilter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}