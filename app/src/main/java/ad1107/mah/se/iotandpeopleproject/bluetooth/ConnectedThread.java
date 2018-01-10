package ad1107.mah.se.iotandpeopleproject.bluetooth;

import ad1107.mah.se.iotandpeopleproject.util.Constants;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.security.auth.login.LoginException;

public class ConnectedThread extends Thread {
  private static final String TAG = "ConnectedThread";
  private final int BUFFERSIZE = 1024;
  private final BluetoothSocket mmSocket;
  private Handler mHandler;
  private final InputStream inStream;
  private final OutputStream outStream;
  private int myCounter;

  public ConnectedThread(BluetoothSocket btSocket, Handler mHandler) {
    mmSocket = btSocket;
    this.mHandler = mHandler;
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
    int counter = 0;
    try {
      initSensor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    int begin = 0;
    int bytes = 0;

    while (true) {
      try {
        bytes += inStream.read(buffer, bytes, buffer.length - bytes);
        for (int i = begin; i < bytes; i++) {
          if (Constants.DEBUG) Log.d("BT_BUFFER", "received  inputData, byteNbr: " + i);

          if (buffer[i] == "\n".getBytes()[0]) {
            mHandler.obtainMessage(1, begin, i, buffer.clone()).sendToTarget();

            begin = i + 1;
            if (i == bytes - 1) {
              bytes = 0;
              begin = 0;
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        break;
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }
  }

  /**
   * The method intitalizes the sensor with the settings used to create the live instances.
   */
  private synchronized void initSensor() throws IOException, InterruptedException {
    String sensetivity = "s" + Constants.SENSITIVITY;
    String frequency = "f" + Constants.FREQUENCY;
    String windowSize = "w" + Constants.WINDOW_SIZE;

    Log.i(TAG, "initSensor: Sending sensor settings sensor");

    // Write frequency setting
    write(frequency.getBytes());
    wait(2 * 1000);
    // Write Sensetivity
    write(sensetivity.getBytes());
    wait(2 * 1000);
    // Write Window Size
    write(windowSize.getBytes());
    wait(2 * 1000);
    Log.i(TAG, "initSensor: Done Sending sensor settings.");
  }

  public void write(byte[] bytes) {
    try {
      outStream.write(bytes);
      outStream.flush();
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
