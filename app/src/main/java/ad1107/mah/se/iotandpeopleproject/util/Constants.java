package ad1107.mah.se.iotandpeopleproject.util;

import java.util.UUID;

/**
 * Created by patriklarsson on 15/12/2017.
 */

public class Constants {

  // Default values for the sensor.

  /**
   * A Constant for the default value of the sensetivity for the IoT Device we are using.
   */
  public static final int SENSITIVITY = 10000;

  /**
   * A Constant for the default value of the sliding window size.
   */
  public static final int WINDOW_SIZE = 30;

  /**
   * A Constant for the defualt value of the frequency for the iot device.
   */
  public static final int FREQUENCY = 30;

  /**
   * A Constant for hardcoded bluetooth UUID.
   */
  public static final UUID BLUETOOTH_UUID = UUID.fromString("");

  /**
   * A Constant for the buffer size.
   */
  public static final int BUFFER_SIZE  = 1024;

  /**
   * A boolean flag indicating if the run is in debug.
   */
  public static final boolean DEBUG = true;

  public static final int NBR_OF_VALS = 180;

  /**
   * URL TO MQTT SERVER and port
   */
  public static final String BROKER_URL = "tcp://m14.cloudmqtt.com:17000";
  /**
   * User name MQTT
   */
  public static final String USER_NAME ="tkgnaxkd";
  /**
   * Password MQTT
   */
  public static final String PASSWORD ="beeJYOZ0G1yn";
  /**
   * Topic
   */
  public static final String PUBLISH_TOPIC = "group_eighth/gesture";

  public static final String PUBLISH_TOPIC2 = "group_ten/handshake";
  /**
   * ID
   */
  public static final String CLIENT_ID = "group_8_android";

}
