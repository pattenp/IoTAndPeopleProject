package ad1107.mah.se.iotandpeopleproject.ui.dialogs;

import ad1107.mah.se.iotandpeopleproject.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

@SuppressLint("ValidFragment") public class BluetoothDevicesAlertDialog extends DialogFragment {
  private static final String TAG = "BluetoothDevicesAlertDi";
  private Activity mActivity;
  private Context mContext;
  private ArrayAdapter<BluetoothDevice> arrayAdapter;
  private Callback callback;

  public BluetoothDevicesAlertDialog(Callback callback, ArrayAdapter adapter, Context context,
      Activity activity) {
    mActivity = activity;
    mContext = context;
    arrayAdapter = adapter;
    this.callback = callback;
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "onClick: " + which);
            callback.itemsSelected(which);
          }
        });
    return builder.create();
  }

  public interface Callback {
    void itemsSelected(int which);
  }
}