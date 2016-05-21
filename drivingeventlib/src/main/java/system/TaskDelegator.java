package system;

import android.content.Intent;
import android.hardware.SensorEvent;

/**
 * Represents a class where tasks can be delegated. Each implementation is responsible
 * for the handling of a particular task. Each class should only complete one task
 * (Single Responsibility Principle)
 */
public interface TaskDelegator {

    public void handleTask(Object object);
    public void handleTask(float[] values);
    public void handleTask(SensorEvent event);
    public void handleTask(Intent intent);
}
