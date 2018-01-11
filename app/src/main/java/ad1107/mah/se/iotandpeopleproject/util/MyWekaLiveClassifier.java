package ad1107.mah.se.iotandpeopleproject.util;

import ad1107.mah.se.iotandpeopleproject.MainActivity;
import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import weka.classifiers.functions.MultilayerPerceptron;
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
  //private J48 classifier;
  private MultilayerPerceptron classifier;
  private final ArrayList<Attribute> attributes;
  private final ArrayList<String> classValues;
  private Instances train;
  private MainActivity activity;
  private Instances unlabeled;

  public MyWekaLiveClassifier(MainActivity activity) {
    this.activity = activity;
    try {
      init();
    } catch (IOException e) {
      e.printStackTrace();
    }
    attributes = new ArrayList<>();
    for (int i = 1; i < 31; i++){
      attributes.add(new Attribute("AccX" + i));
      attributes.add(new Attribute("AccY" + i));
      attributes.add(new Attribute("AccZ" + i));
      attributes.add(new Attribute("GyrX" + i));
      attributes.add(new Attribute("GyrY" + i));
      attributes.add(new Attribute("GyrZ" + i));
    }

    classValues = new ArrayList<>();
    classValues.add("up");
    classValues.add("right");
    classValues.add("left");
    classValues.add("down");
    attributes.add(new Attribute("gesture", classValues));
  }

  private void init() throws IOException {
    // Build the training data

    AssetManager assestManger = activity.getAssets();
    InputStream in = assestManger.open("Jonte_No_Norm.arff");
    ConverterUtils.DataSource source = new ConverterUtils.DataSource(in);

    try {
      train = source.getDataSet();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    train.setClassIndex(train.numAttributes() -1);

    // J48
     //String[] options = new String[1];
     //classifier = new J48();         // new instance of classifier
     //try {
      //classifier.setOptions(options);     // set the options
      //classifier.buildClassifier(train);   // build classifier
      //} catch (Exception e) {
      //e.printStackTrace();
    //}

    // Multilayer perception
    classifier = new MultilayerPerceptron();
    //Setting Parameters
    classifier.setLearningRate(0.1);
    classifier.setMomentum(0.2);
    classifier.setTrainingTime(2000);
    classifier.setHiddenLayers("3");
    try {
      classifier.buildClassifier(train);
    } catch (Exception e) {
     e.printStackTrace();
    }
  }

  public Instances createLiveInstance(double[] data) {
    double[] vals = new double[181];
    for (int i = 0; i < data.length; i++) {
      vals[i] = data[i];
    }
    DenseInstance instance = new DenseInstance(1.0,vals);

    unlabeled = new Instances("testData", attributes, 0);
    unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
    unlabeled.add(instance);
    return unlabeled;
  }

  public void classify(Instances unlabeled){
    double clsLabel = 0;
    try {
      clsLabel = classifier.classifyInstance(unlabeled.instance(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
    unlabeled.instance(0).setClassValue(clsLabel);
    int classIndex = train.numAttributes() - 1;
    Log.d(TAG, "Detected Gesture: " + unlabeled.instance(0).attribute(classIndex).value((int) clsLabel));
    activity.publishGesture(unlabeled.instance(0).attribute(classIndex).value((int) clsLabel));
  }
}
