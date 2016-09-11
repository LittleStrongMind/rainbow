package little.rainbow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


/**
RainbowSplashActivity activité splash - à but purement esthétique
**/ 
public class RainbowSplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rainbowsplash_activity);


        TextView prism = (TextView) findViewById(R.id.prism_logo);
        TextView build_version = (TextView) findViewById(R.id.build_version);


        Typeface prism_font = Typeface.createFromAsset(this.getAssets(), "fonts/UnGungseo.ttf");

        prism.setTypeface(prism_font);
        build_version.setText("v" + String.valueOf(RainbowCoreActivity.BUILD_VERSION));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RainbowSplashActivity.this, RainbowMenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            RainbowCoreActivity.toggleUi(this);

    }
}
