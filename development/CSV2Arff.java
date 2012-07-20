import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.FastVector;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;

public class CSV2Arff {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   */
  public static void main(String[] args) throws Exception
  {
    if (args.length != 2) {
      System.out.println("\nUsage: CSV2Arff <input.csv> <output.arff>\n");
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

    // Add clicked attribute
    FastVector yesNo = new FastVector(2);
    yesNo.addElement("yes");
    yesNo.addElement("no");
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