package ad1107.mah.se.iotandpeopleproject.preprocessing;

import android.util.Log;

import java.util.List;

import ad1107.mah.se.iotandpeopleproject.util.Constants;

import static ad1107.mah.se.iotandpeopleproject.util.Constants.*;

/**
 * Created by Jonte on 10-Jan-18.
 */

public class MinMax {

    private double newMax;
    private double newMin;
    private double[] oldMin = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
    private double[] oldMax = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

    public double[] prepareForMinMax(List<double[]> input){
        double[] output = new double[180];
        int counter = 0;
        for (int i = 0; i < input.size(); i++) {
            double[] arr = input.get(i);
            for (int j = 0; j < arr.length; j++) {
                output[counter] = arr[j];
                counter++;
            }
        }
        return output;
    }

    public synchronized double[] minMax(double[] vals) {

        newMax = 100;
        newMin = -100;

        //1. Find old min/max for each category
        for (int i = 0; i <= NBR_OF_VALS - 6; i += 6) {

            //Check AccX
            if (vals[i] < oldMin[0]) {
                oldMin[0] = vals[i];
            }
            if (vals[i] > oldMax[0]) {
                oldMax[0] = vals[i];
            }

            //Check AccY
            if (vals[i + 1] < oldMin[1]) {
                oldMin[1] = vals[i + 1];
            }
            if (vals[i + 1] > oldMax[0]) {
                oldMax[1] = vals[i + 1];
            }

            //Check AccZ
            if (vals[i + 2] < oldMin[2]) {
                oldMin[2] = vals[i + 2];
            }
            if (vals[i + 2] > oldMax[2]) {
                oldMax[2] = vals[i + 2];
            }

            //Check GyrX
            if (vals[i + 3] < oldMin[3]) {
                oldMin[3] = vals[i + 3];
            }
            if (vals[i + 3] > oldMax[3]) {
                oldMax[3] = vals[i + 3];
            }

            //Check GyrY
            if (vals[i + 4] < oldMin[4]) {
                oldMin[4] = vals[i + 4];
            }
            if (vals[i + 4] > oldMax[4]) {
                oldMax[4] = vals[i + 4];
            }

            //Check GyrZ
            if (vals[i + 5] < oldMin[5]) {
                oldMin[5] = vals[i + 5];
            }
            if (vals[i + 5] > oldMax[5]) {
                oldMax[5] = vals[i + 5];
            }
        }

        int indexToOldMinMaxVals = 0;

        //2. Normalize each data point in arr.
        for (int i = 0; i < vals.length; i++) {


            if (indexToOldMinMaxVals % 6 == 0) {
                indexToOldMinMaxVals = 0;
            }

            Log.d("BT_MinMax", "old val: " + vals[i] + "\noldMin: " + oldMin[indexToOldMinMaxVals] + "\noldMax: " + oldMax[indexToOldMinMaxVals]);
            vals[i] = ((vals[i] - oldMin[indexToOldMinMaxVals])
                    / (oldMax[indexToOldMinMaxVals] - oldMin[indexToOldMinMaxVals])) * (newMax - newMin) + newMin;

            indexToOldMinMaxVals++;
            Log.d("BT_MinMax", "new val: " + vals[i]);
        }
        return vals;

    }
}
