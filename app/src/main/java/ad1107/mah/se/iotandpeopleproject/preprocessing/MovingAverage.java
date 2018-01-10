package ad1107.mah.se.iotandpeopleproject.preprocessing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Jonte on 10-Jan-18.
 */

public class MovingAverage {

    private LinkedList values = new LinkedList();

    private int length;

    private double sum = 0;

    private double average = 0;

    /**
     * @param length the maximum length
     */
    public MovingAverage(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.length = length;

    }

    public double currentAverage() {
        return average;
    }

    /**
     * Compute the moving average.
     * Synchronised so that no changes in the underlying data is made during calculation.
     *
     * @param value The value
     * @return The average
     */
    public synchronized double compute(double value) {
        if (values.size() == length && length > 0) {
            sum -= ((Double) values.getFirst()).doubleValue();
            values.removeFirst();
        }
        sum += value;
        values.addLast(new Double(value));
        average = sum / values.size();
        return average;
    }

}
