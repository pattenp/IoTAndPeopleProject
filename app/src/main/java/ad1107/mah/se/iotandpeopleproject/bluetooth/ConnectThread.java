package ad1107.mah.se.iotandpeopleproject.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;
import java.io.IOException;

/**
 * The class is used to the bluetooth device.
 * @author Patrik Larsson (AD1107)
 */
public class ConnectThread extends Thread {
  private static final String TAG = "ConnectThread";
  private final BluetoothSocket btSocket;
  private final BluetoothDevice btDevice;
  private final BluetoothAdapter btAdapter;

  public ConnectThread(BluetoothDevice btDevice, int which, BluetoothAdapter btAdapter) {
    super("Bluetooth Connect Thread");
    this.btAdapter = btAdapter;
    this.btDevice = btDevice;
    BluetoothSocket tmp = null;

    try {
      btDevice.createRfcommSocketToServiceRecord(btDevice.getUuids()[which].getUuid());
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "ConnectThread: Failed to create bluetoothsocket", e);
    }
    btSocket = tmp;
  }

  @Override public void run() {
    btAdapter.cancelDiscovery();
    try {
      btSocket.connect();
    } catch (IOException connectException) {
      connectException.printStackTrace();
      Log.e(TAG, "run: Failed to connect to btSocket", connectException);
      try {
        btSocket.close();
      } catch (IOException closeConnection) {
        closeConnection.printStackTrace();
        Log.e(TAG, "run: Failed to close btSocket", closeConnection);
      }
    }
  }
}
