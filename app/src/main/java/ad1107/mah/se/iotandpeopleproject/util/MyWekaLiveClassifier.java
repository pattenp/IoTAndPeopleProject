package ad1107.mah.se.iotandpeopleproject.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * Created by patriklarsson on 04/01/2018.
 */

public class MyWekaLiveClassifier {
  private static final String TAG = "MyWekaLiveClassifier";
  private static final String trainpath = "";
  private J48 tree;
  private final ArrayList<Attribute> attributes;
  private final ArrayList<String> classValues;
  private Instances train;
  private Activity activity;
  private Instances unlabeled;

  public MyWekaLiveClassifier(Activity activity) {
    this.activity = activity;
    try {
      init();
    } catch (IOException e) {
      e.printStackTrace();
    }
    attributes = new ArrayList<>();
    for (int i = 1; i < 21; i++){
      attributes.add(new Attribute("AccX" + i));
      attributes.add(new Attribute("AccY" + i));
      attributes.add(new Attribute("AccZ" + i));
      attributes.add(new Attribute("GyrX" + i));
      attributes.add(new Attribute("GyrY" + i));
      attributes.add(new Attribute("GyrZ" + i));
    }

    classValues = new ArrayList<>();
    classValues.add("up");
    classValues.add("down");
    classValues.add("left");
    classValues.add("right");
    attributes.add(new Attribute("gesture", classValues));
  }

  private void init() throws IOException {
    // Build the training data
    AssetManager assestManger = activity.getAssets();
    InputStream in = assestManger.open("MergeTrainSet.arff");
    ConverterUtils.DataSource source = new ConverterUtils.DataSource(in);

    try {
      train = source.getDataSet();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    train.setClassIndex(train.numAttributes() -1);
  }

  public Instances createLiveInstance(ArrayList<String[]> data) {
    DenseInstance instance = new DenseInstance(181);
    for (int i = 0; i < 30; i++) {
      String[] vals = data.get(i);
      instance.setValue(i++, vals[0]);
      instance.setValue(i++, vals[1]);
      instance.setValue(i++, vals[2]);
      instance.setValue(i++, vals[3]);
      instance.setValue(i++, vals[4]);
      instance.setValue(i++, vals[5]);
    }

    unlabeled = new Instances("testData", attributes, 180);
    unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
    unlabeled.add(instance);
    return unlabeled;
  }

  public void classify(Instances unlabeled){
    double clsLabel = 0;
    try {
      clsLabel = tree.classifyInstance(unlabeled.instance(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
    unlabeled.instance(0).setClassValue(clsLabel);
    int classIndex = train.numAttributes() - 1;
    Log.d(TAG, "Detected Gesture: " + unlabeled.instance(0).attribute(classIndex).value((int) clsLabel));
  }
}
