import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.List;
import java.util.LinkedList;

import java.io.File;

public class MetricsToRate {
  /**
   * takes 1 argument:
   * - input file
   */
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1) {
      System.out.println("\nUsage: MetricsToRate <input>\n");
      System.exit(1);
    }

    // load data
    DataSource source = new DataSource(args[0]);
    Instances data = source.getDataSet();

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
        inst.setValue(attributeIndex(ctr, data), val);
    }

    data.deleteAttributeAt(attributeIndex(clicks, data));
    data.deleteAttributeAt(attributeIndex(impressions, data));

    System.out.println(data);
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