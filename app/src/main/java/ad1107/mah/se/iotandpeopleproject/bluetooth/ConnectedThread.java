package ad1107.mah.se.iotandpeopleproject.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
  private static final String TAG = "ConnectedThread";
  private final int BUFFERSIZE = 1024;
  private final BluetoothSocket mmSocket;
  private final InputStream inStream;
  private final OutputStream outStream;

  public ConnectedThread(BluetoothSocket btSocket) {
    mmSocket = btSocket;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    try {
      tmpIn = btSocket.getInputStream();
      tmpOut = btSocket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "ConnectedThread: Failed to create streams", e);
    }

    inStream = tmpIn;
    outStream = tmpOut;
  }

  @Override public void run() {
    byte[] buffer = new byte[BUFFERSIZE];
    int begin = 0;
    int bytes = 0;

    while (true) {
      try {
        bytes += inStream.read(buffer, begin, bytes);
        for (int i = begin; i < bytes; i++) {
          // TODO Write the code in here to read the data from bluetooth.
        }
      } catch (IOException e) {
        e.printStackTrace();
        Log.e(TAG, "run: Something went wrong reading bytes", e);
      }
    }
  }

  public void write(byte[] bytes) {
    try {
      outStream.write(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void cancel() {
    try {
      mmSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
