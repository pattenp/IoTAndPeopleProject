package ad1107.mah.se.iotandpeopleproject;

import ad1107.mah.se.iotandpeopleproject.R;
import ad1107.mah.se.iotandpeopleproject.bluetooth.BluetoothManager;
import ad1107.mah.se.iotandpeopleproject.preprocessing.MinMax;
import ad1107.mah.se.iotandpeopleproject.preprocessing.MovingAverage;
import ad1107.mah.se.iotandpeopleproject.util.Constants;
import ad1107.mah.se.iotandpeopleproject.util.MyWekaLiveClassifier;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements MqttCallback {
  private static final String TAG = "MainActivity";
  private BluetoothManager btManager;
  private MyWekaLiveClassifier myWekaLiveClassifier;
  private ArrayList<double[]> inputs = new ArrayList<>();
  private MovingAverage[] movingAverages = new MovingAverage[6];
  private MinMax minMax = new MinMax();
  private PahoMqttClient pahoMqttClient;
  private MqttAndroidClient mqqtClient;
  private boolean subscribed = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    pahoMqttClient = new PahoMqttClient();
    mqqtClient = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.BROKER_URL,
        Constants.CLIENT_ID);
    mqqtClient.setCallback(new MqttCallbackExtended() {
      @Override public void connectComplete(boolean b, String s) {
        Log.d(TAG, "connectComplete: " + s);
      }

      @Override public void connectionLost(Throwable throwable) {
        Log.e(TAG, "connectionLost: ", throwable);
      }

      @Override public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, "messageArrived: " + s);
      }

      @Override public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
          Log.d(TAG, "deliveryComplete: " + iMqttDeliveryToken.getMessage().toString());
        } catch (MqttException e) {
          e.printStackTrace();
        }
      }
    });

    myWekaLiveClassifier = new MyWekaLiveClassifier(this);
    // Creates one moving average per column.
    //for (int i = 0; i < movingAverages.length; i++) {
    //  movingAverages[i] = new MovingAverage(5);
    //}
    @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
      @Override public void handleMessage(Message msg) {
        byte[] writeBuf = (byte[]) msg.obj;
        int begin = (int) msg.arg1;
        int end = (int) msg.arg2;

        switch (msg.what) {
          case 1:
            String writeMessage = new String(writeBuf);
            writeMessage = writeMessage.substring(begin, end);
            Log.d(TAG, "String in sent in to handler: " + writeMessage);
            if (writeMessage.charAt(0) == 'h') {
              String[] split = writeMessage.substring(2, writeMessage.length()).split(",");
              double[] input = convertToDoubleArr(split);
              inputs.add(input);
              if (inputs.size() == 30) {
                preProcess(inputs);
                inputs.clear();
              }
              break;
            }
        }
      }
    };

    // Intialize Bluetooth.
    btManager = new BluetoothManager(this, this, mHandler);
    btManager.init();
  }

  public double[] convertToDoubleArr(String[] arr) {
    // TODO Quickfix
    if (arr.length > 6) {
      String[] temp = arr.clone();
      arr = new String[6];
      for (int i = 0; i < arr.length; i++) {
        arr[i] = temp[i];
      }
    } else if(arr.length < 6) {
      int counter = 0;
      String[] temp = arr.clone();
      arr = new String[6];
      for (int i = 0; i < temp.length && counter < 6; i++) {
        arr[counter++] = temp[i];
      }
      for (int i = counter; i < 6; i++) {
        arr[i] = "0";
      }
    }
    double[] output = new double[arr.length];
   // int subCounter= 0;
    for (int i = 0; i < arr.length; i++) {
     // String temp = arr[i];
      //for (int j = 0; j <temp.length() ; j++) {
        //if(temp.charAt(j) == '-'&& temp.charAt(j)!=0){
         // arr[i] =temp.substring(j);
        //}
      //}
      output[i] = Double.parseDouble(arr[i]);
    }
    return output;
  }

  public void preProcess(List<double[]> input) {
    // Moving average
    //for (int i = 0; i < input.size(); i++) {
    //  double[] arr = inputs.get(i);
    // for (int j = 0; j < arr.length; j++) {
    //    arr[j] = movingAverages[j].compute(arr[j]);
    //  }
    //}

    double[] preparedArr = minMax.prepareForMinMax(inputs);
    //Min-max Normalization
    //preparedArr = minMax.minMax(preparedArr);
    myWekaLiveClassifier.classify(myWekaLiveClassifier.createLiveInstance(preparedArr));
  }

  public void publishGesture(String message) throws MqttException {
    if (mqqtClient.isConnected() && !subscribed) {
      subscribeToTopics();
      subscribed = true;
    }
    try {
      pahoMqttClient.publishMessage(mqqtClient, message, 1, Constants.PUBLISH_TOPIC);
      pahoMqttClient.publishMessage(mqqtClient, message, 1, Constants.PUBLISH_TOPIC2);
    } catch (MqttException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  private void subscribeToTopics() throws MqttException {
    pahoMqttClient.subscribe(mqqtClient, "group_ten/handshake", 1);
  }

  @Override public void connectionLost(Throwable throwable) {

  }

  @Override public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
    Log.d(TAG, "messageArrived: " + s);
  }

  @Override public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

  }
}

