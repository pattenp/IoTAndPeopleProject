package ad1107.mah.se.iotandpeopleproject.ui.activities;

import ad1107.mah.se.iotandpeopleproject.R;
import ad1107.mah.se.iotandpeopleproject.bluetooth.BluetoothManager;
import ad1107.mah.se.iotandpeopleproject.preprocessing.MinMax;
import ad1107.mah.se.iotandpeopleproject.preprocessing.MovingAverage;
import ad1107.mah.se.iotandpeopleproject.util.MyWekaLiveClassifier;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothManager btManager;
    private MyWekaLiveClassifier myWekaLiveClassifier;
    private ArrayList<double[]> inputs = new ArrayList<>();
    private MovingAverage[] movingAverages = new MovingAverage[6];
    private MinMax minMax = new MinMax();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWekaLiveClassifier = new MyWekaLiveClassifier(this);

        // Creates one moving average per column.
        for (int i = 0; i < movingAverages.length; i++) {
            movingAverages[i] = new MovingAverage(5);
        }
        @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;


                switch (msg.what) {
                    case 1:
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        Log.d("InData", "String in sent in to handler: " + writeMessage);
                        if (writeMessage.charAt(0) == ',') {
                            String[] split = writeMessage.substring(1, writeMessage.length() - 1).split(",");
                            double[] input = convertToDoubleArr(split);
                            inputs.add(input);
                            if(inputs.size() == 30){
                               preProcess(inputs);
                            }

                            break;
                        }
                }
            }
        };


        // TODO Remove this code only here to test.
        btManager = new BluetoothManager(this, this, mHandler);
        btManager.init();

        // TODO Remove this code only here to start configure activity the lazy way.
        // Intent intent = new Intent(this, ConfigurationActivity.class);
        // startActivity(intent);
    }

    public double[] convertToDoubleArr(String[] arr) {
        if (arr.length != 6) {
            throw new IllegalArgumentException("Array is not length 6 ");
        }
        double[] output = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            output[i] = Double.parseDouble(arr[i]);
        }
        return output;
    }

    public void preProcess(List<double[]> input) {
        // Moving average
        for (int i = 0; i < input.size(); i++) {
            double[]arr = inputs.get(i);
            for (int j = 0; j <arr.length ; j++) {
              arr[j] = movingAverages[j].compute(arr[j]);
            }
        }

        double[] preparedArr = minMax.prepareForMinMax(inputs);
        //Min-max Normalization
        preparedArr = minMax.minMax(preparedArr);
        myWekaLiveClassifier.classify(myWekaLiveClassifier.createLiveInstance(preparedArr));
        inputs.clear();
    }
}
