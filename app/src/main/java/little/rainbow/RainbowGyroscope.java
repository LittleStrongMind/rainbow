package little.rainbow;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
RainbowGyroscope gère la récupération des données gyroscopique
**/
public class RainbowGyroscope implements SensorEventListener  {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float gyr_x;
    private float gyr_y;
    private float gyr_z;

    private Activity activity;

    public RainbowGyroscope(Activity activity){
        this.activity = activity;

        mSensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gyr_x = 0.0f;
        gyr_y = 0.0f;
        gyr_z = 0.0f;
    }


    public void pause(){
        mSensorManager.unregisterListener(this);
    }

    public void stop(){
        mSensorManager.unregisterListener(this);

    }

    public void resume(){
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                mSensorManager.SENSOR_DELAY_NORMAL);
    }

    public String getGyroscopeData(){
        return gyr_x + ":" + gyr_y + ":" + gyr_z;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        gyr_x = event.values[0];
        gyr_y = event.values[1];
        gyr_z = event.values[2];


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
