package ad1107.mah.se.iotandpeopleproject.ui.activities;

import ad1107.mah.se.iotandpeopleproject.R;
import ad1107.mah.se.iotandpeopleproject.bluetooth.BluetoothManager;
import ad1107.mah.se.iotandpeopleproject.util.MyWekaLiveClassifier;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private BluetoothManager btManager;
  private MyWekaLiveClassifier myWekaLiveClassifier;
  private ArrayList<String[]> inputs = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    myWekaLiveClassifier = new MyWekaLiveClassifier(this);

    @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
      @Override public void handleMessage(Message msg) {
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
              inputs.add(split);
              if (inputs.size() == 30) {
                  myWekaLiveClassifier.classify(myWekaLiveClassifier.createLiveInstance(inputs));
                  inputs.clear();
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
}
