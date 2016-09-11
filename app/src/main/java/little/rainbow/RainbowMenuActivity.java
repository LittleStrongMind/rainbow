package little.rainbow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
RainbowMenuActivity gère la configuration de l'application avant l'affichage
du flux vidéo
**/
public class RainbowMenuActivity extends Activity {


    private LinearLayout logPanel;

    private volatile Thread rainbowServerThreadTCP;
    private Thread rainbowServerThreadUDP;

    private RainbowServerRunnableTCP rainbowServerRunnableTCP;
    private RainbowServerRunnableUDP rainbowServerRunnableUDP;

    private RainbowGyroscope rainbowGyroscope;

    public Thread getRainbowServerThreadTCP(){return rainbowServerThreadTCP; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RainbowCoreActivity.toggleUi(this);
        setContentView(R.layout.rainbowmenu_activity);

        customize();

        // Panneau ou sont affichés les logs
        logPanel = (LinearLayout) findViewById(R.id.logpanel);

        startup();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            RainbowCoreActivity.toggleUi(this);

    }

    private void customize(){

        TextView prism = (TextView) findViewById(R.id.prism_logo);
        TextView build_version = (TextView) findViewById(R.id.build_version);

        Typeface prism_font = Typeface.createFromAsset(this.getAssets(), "fonts/UnGungseo.ttf");

        prism.setTypeface(prism_font);
        build_version.setText("v" + String.valueOf(RainbowCoreActivity.BUILD_VERSION));
    }

    public void logMessage(final String message){

        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView messagesTextView = new TextView(activity);
                messagesTextView.setText("prism@rainbow:~$ " + message);
                logPanel.addView(messagesTextView);

            }
        });

    }

    public String executeCMD(String cmd){
        StringBuffer output = new StringBuffer();
        try{
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            process.waitFor();


        } catch(IOException e){
            e.printStackTrace();

        } catch(InterruptedException e){
            e.printStackTrace();
        }

        return output.toString();

    }

    public void clearLogPanel(){
        logPanel.removeAllViewsInLayout();
    }
    public void startServerTCP(){

        rainbowServerRunnableTCP = new RainbowServerRunnableTCP(this);
        rainbowServerThreadTCP = new Thread(rainbowServerRunnableTCP);
        rainbowServerThreadTCP.start();
    }

    public void startServerUDP(){

        rainbowServerRunnableUDP = new RainbowServerRunnableUDP(this);
        rainbowServerThreadUDP = new Thread(rainbowServerRunnableUDP);
        rainbowServerThreadUDP.start();
    }

    public String getRes(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //return metrics.widthPixels + "x" + metrics.heightPixels;
        return (int) metrics.widthPixels/2 + "x" + (int) metrics.heightPixels/2;
    }

    public String getGyr(){
        return rainbowGyroscope.getGyroscopeData();
    }
    public void stopServerTCP(){
        if(rainbowServerThreadTCP != null && !rainbowServerThreadTCP.isInterrupted() && rainbowServerRunnableTCP != null) rainbowServerRunnableTCP.stop();
    }
    public void stopServerUDP(){
        if(rainbowServerThreadUDP != null && !rainbowServerThreadUDP.isInterrupted() && rainbowServerRunnableUDP != null) rainbowServerRunnableUDP.stop();
    }

    public boolean checkWifi(){

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean wifiState = wifi.isConnected();
        if(!wifiState){ logMessage("WIFI IS DOWN"); }
        else { logMessage("WIFI OK"); }

        return wifiState;
    }

    public void restart(View view){
        stopServerUDP();
        stopServerTCP();
        clearLogPanel();
        startup();
    }


    public void startup(){


        if(!checkWifi()){
            logMessage("REACTIVATE IT NOW");
        }
        logMessage("DEVICE IP " + NetworkUtils.getIPAddress(true));
        rainbowGyroscope = new RainbowGyroscope(this);
        rainbowGyroscope.resume();
        startServerTCP();




    }
}
