package ad1107.mah.se.iotandpeopleproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MqttService extends Service {


  public MqttService() {
  }

  @Override public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
