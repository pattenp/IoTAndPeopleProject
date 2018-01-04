package ad1107.mah.se.iotandpeopleproject.bluetooth;

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

public class ConnectedThread extends Thread {
  private static final String TAG = "ConnectedThread";
  private final int BUFFERSIZE = 1024;
  private final BluetoothSocket mmSocket;
  private Handler mHandler;
  private final InputStream inStream;
  private final OutputStream outStream;

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
    byte[] buffer = new byte[1024];
    int begin = 0;
    int bytes = 0;
    while (true) {
      try {
        bytes += inStream.read(buffer, bytes, buffer.length - bytes);
        for (int i = begin; i < bytes; i++) {
          if (buffer[i] == "h".getBytes()[0]) {
            String s = new String(buffer, begin, bytes);
            Log.i(TAG, "run: " + s);
            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
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
      }
    }
  }

  public void run2() {
    byte[] buffer = new byte[BUFFERSIZE];
    int begin = 0;
    int bytes = 0;

    // write("f30".getBytes());
    // write("w30".getBytes());
    while (true) {
      try {
        bytes += inStream.read(buffer, bytes, buffer.length - bytes);
        for (int i = begin; i < bytes; i++) {
          if (buffer[i] == "h".getBytes()[0]) {
            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
            begin = i + 1;
            if (i == bytes - 1) {
              bytes = 0;
              begin = 0;
            }
          }
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
