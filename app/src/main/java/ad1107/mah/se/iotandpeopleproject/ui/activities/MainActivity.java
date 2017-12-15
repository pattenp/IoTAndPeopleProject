package ad1107.mah.se.iotandpeopleproject.ui.activities;

import ad1107.mah.se.iotandpeopleproject.R;
import ad1107.mah.se.iotandpeopleproject.bluetooth.BluetoothManager;
import ad1107.mah.se.iotandpeopleproject.configuration.ConfigurationActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // TODO Remove this code only here to test.
    BluetoothManager btManager = new BluetoothManager(this, this);
    btManager.init();

    // TODO Remove this code only here to start configure activity the lazy way.
    // Intent intent = new Intent(this, ConfigurationActivity.class);
    // startActivity(intent);
  }
}
