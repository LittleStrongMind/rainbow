package little.rainbow;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
RainbowCoreActivity est l'activité orchestre de Rainbow
**/
public class RainbowCoreActivity extends Activity {

    public static double BUILD_VERSION = 0.01;

    public static int TCP_PORT = 8000;
    public static int UDP_PORT = 8000;

    public static String END_CHAR = "__END__";
    private RainbowGyroscope rainbowGyroscope;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        toggleUi(this);
        setContentView(R.layout.rainbowcore_activity);

        rainbowGyroscope = new RainbowGyroscope(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            toggleUi(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        rainbowGyroscope.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rainbowGyroscope.pause();

    }

    @Override
    protected void onStop(){
        super.onStop();
        rainbowGyroscope.stop();
    }

    public static void toggleUi(Activity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
