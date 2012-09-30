import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.List;
import java.util.LinkedList;

import java.io.File;

public class WeightedMetrics {
  /**
   * takes 1 arguments:
   * - input file
   */
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1) {
      System.out.println("\nUsage: WeightedMetrics <input>\n");
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

    // Add clicked attribute
    List<String> yesNo = new LinkedList<String>();
    yesNo.add("yes");
    yesNo.add("no");
    Attribute click = new Attribute("Click", yesNo);
    data.insertAttributeAt(click, data.numAttributes());

    int length = data.numInstances();
    for (int i = 0; i < length; i++) {
        Instance yes = data.instance(i);
        Instance no = (Instance)yes.copy();
        yes.setValue(attributeIndex(click, data), "yes");
        no.setValue(attributeIndex(click, data), "no");
        yes.setWeight(1.0*yes.value(clicks));
        no.setWeight(1.0*yes.value(impressions)-yes.value(clicks));
        data.add(no);
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