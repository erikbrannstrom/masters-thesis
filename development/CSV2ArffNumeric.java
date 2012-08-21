import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.util.List;
import java.util.LinkedList;

import java.io.File;

public class CSV2ArffNumeric {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   */
  public static void main(String[] args) throws Exception
  {
    if (args.length != 2) {
      System.out.println("\nUsage: CSV2ArffNumeric <input.csv> <output.arff>\n");
      System.exit(1);
    }

    // load CSV
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File(args[0]));
    Instances data = loader.getDataSet();

    Attribute clicks = data.attribute("Clicks");
    Attribute impressions = data.attribute("Impressions");

    if (clicks == null || impressions == null) {
        System.out.println("Could not find attributes for clicks and/or impressions.");
        System.exit(1);
    }

    // Add CTR%
    Attribute ctr = new Attribute("CTR");
    data.insertAttributeAt(ctr, data.numAttributes());

    for (int i = 0; i < data.numInstances(); i++) {
        Instance inst = data.instance(i);
        double val = inst.value(clicks)/inst.value(impressions);
        inst.setValue(attributeIndex(ctr, data), 10000.0*val);
    }

    data.deleteAttributeAt(attributeIndex(clicks, data));
    data.deleteAttributeAt(attributeIndex(impressions, data));

    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);
    saver.setFile(new File(args[1]));
    saver.setDestination(new File(args[1]));
    saver.writeBatch();
  }

  private static int attributeIndex(weka.core.Attribute att, Instances data)
  {
    for (int i = 0; i < data.numAttributes(); i++) {
        if (data.attribute(i).equals(att)) {
            return i;
        }
    }
    return -1;
  }

}