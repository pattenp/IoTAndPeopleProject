package ad1107.mah.se.iotandpeopleproject.bluetooth;

import ad1107.mah.se.iotandpeopleproject.R;
import ad1107.mah.se.iotandpeopleproject.ui.dialogs.BluetoothDevicesAlertDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The class contains the code for handling bluetooth connection.
 */
public class BluetoothManager implements BluetoothDevicesAlertDialog.Callback {
  private static final String TAG = "BluetoothManager";

  private final BluetoothAdapter mBtAdatper =
      android.bluetooth.BluetoothAdapter.getDefaultAdapter();
  private final Activity mActivity;
  private Handler mHandler;

  private final Context mContext;
  private ArrayAdapter<String> arrayAdapter;
  private Set<BluetoothDevice> pairedDevices;

  public BluetoothManager(Context context, Activity activity, Handler mHandler) {
    mContext = context;
    mActivity = activity;
    this.mHandler = mHandler;
  }

  /**
   * Checks if the device supports bluetooth and enables bluetooth.
   */
  public void init() {
    if (mBtAdatper != null) {
      if (!mBtAdatper.isEnabled()) {
        Log.i(TAG, "Enabling bluetooth.");
        mBtAdatper.enable();
        Log.i(TAG, "Bluetooth Enabled.");
      }
      getPairedDevices();
    } else {
      Toast.makeText(mContext, "Bluetooth is not available on device", Toast.LENGTH_LONG).show();
    }
  }

  private void getPairedDevices() {
    pairedDevices = mBtAdatper.getBondedDevices();
    List<String> bluetoothNames = new ArrayList<>();
    if (pairedDevices.size() > 0) {
      for (BluetoothDevice bt : pairedDevices) {
        bluetoothNames.add(bt.getName() +"\t" + bt.getAddress());
      }
      arrayAdapter = new ArrayAdapter<String>(mContext,
          android.R.layout.simple_list_item_1, bluetoothNames);
      BluetoothDevicesAlertDialog btDialog = new BluetoothDevicesAlertDialog(this, arrayAdapter
      ,mContext, mActivity);
      btDialog.show(mActivity.getFragmentManager(), null);
    } else {
      Toast.makeText(mContext, "No paired Devices.", Toast.LENGTH_SHORT).show();
    }
  }

  private BluetoothDevice itemSelected(int index) {
    // TODO Test this method
    Iterator<BluetoothDevice> Devices = pairedDevices.iterator();
    int counter = 0;
    while(Devices.hasNext()) {
      if (counter == index) {
        return Devices.next();
      }
      Devices.next();
      counter++;
    }
    return null;
  }

  @Override public void itemsSelected(int which) {
    BluetoothDevice btDevice = itemSelected(which);
    // TODO Start Bluetooth Connection;
    new ConnectThread(btDevice, which, mBtAdatper, mHandler).start();
  }

}
