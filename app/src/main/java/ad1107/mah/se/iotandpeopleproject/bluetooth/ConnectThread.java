package ad1107.mah.se.iotandpeopleproject.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import java.io.IOException;

/**
 * The class is used to the bluetooth device.
 *
 * @author Patrik Larsson (AD1107)
 */
public class ConnectThread extends Thread {
  private static final String TAG = "ConnectThread";
  private final BluetoothSocket btSocket;
  private final BluetoothDevice btDevice;
  private final Handler mHandler;
  private final BluetoothAdapter btAdapter;

  public ConnectThread(BluetoothDevice btDevice, int which, BluetoothAdapter btAdapter,
      Handler mHandler) {
    super("Bluetooth Connect Thread");
    this.btAdapter = btAdapter;
    this.btDevice = btDevice;
    this.mHandler = mHandler;
    BluetoothSocket tmp = null;

    try {
      tmp = btDevice.createRfcommSocketToServiceRecord(btDevice.getUuids()[0].getUuid());
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "ConnectThread: Failed to create bluetoothsocket", e);
    }
    btSocket = tmp;
  }

  @Override public void run() {
    btAdapter.cancelDiscovery();
    try {
      Log.d(TAG, "run: Trying to connect to " + btDevice.getName());
      btSocket.connect();
      ConnectedThread connectedThread = new ConnectedThread(btSocket, mHandler);
      connectedThread.start();
      Log.i(TAG, "run: Bluetooth Connected");
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
