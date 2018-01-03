import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import weka.core.Instances;

/**
 * The class is used to create a classifiers from our input file.
 */
public class GestureRec {
  private static final String trainPath = "";
  private static final String testPath = "";


  /**
   * Creates a data instance from the
   * @param path
   * @return
   * @throws IOException
   */
  private Instances createInsatance(String path) throws IOException {
    BufferedReader bReader = new BufferedReader(new FileReader(trainPath));
    Instances instances = new Instances(bReader);
    instances.setClassIndex(instances.numAttributes() - 1);
    bReader.close();
    return instances;
  }
}
