package little.rainbow;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
RainbowServerRunnableTCP est le ServerTCP
**/
public class RainbowServerRunnableTCP implements Runnable {


    private RainbowMenuActivity activity;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    public RainbowServerRunnableTCP(RainbowMenuActivity activity){
        this.activity = activity;
    }


    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(RainbowCoreActivity.TCP_PORT);
            activity.logMessage("TCP READY localhost:" + RainbowCoreActivity.TCP_PORT);

            clientSocket = serverSocket.accept();
            activity.logMessage("CONNECTION ESTABLISHED");
            Log.d("DEBUG", "CONNECTION ESTABLISHED");

            // on envoie la résolution
            send(activity.getRes());
            activity.logMessage("SCREEN SIZE DONE");
            Log.d("DEBUG", "SCREEN SIZE DONE");

            send(NetworkUtils.getIPAddress(true));
            activity.logMessage("IP ADDRESS DONE");
            Log.d("DEBUG", "IP ADDRESS DONE");

            recv();
            stop();

            activity.startServerUDP();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * envoie la résolution de l'appareil
     */
    private void send(String message){
        try {
            out = new PrintWriter(clientSocket.getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * recoit les messages
     */
    private void recv(){
        try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            boolean done = false;

            String line;
            while(!done){
                line = in.readLine();
                if(line == null || line.equals(RainbowCoreActivity.END_CHAR))
                    done = true;
                else
                    activity.logMessage(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * arrête le serveur proprement
     */
    public void stop(){
        activity.logMessage("TCP SERVER STOPPED");
        Log.d("DEBUG", "TCP SERVER STOPPED");

        try {
            if(serverSocket != null) serverSocket.close();
            if(clientSocket != null) {
                send(RainbowCoreActivity.END_CHAR);
                clientSocket.close();
            }
            if(in != null) in.close();
        } catch (IOException e) {
            activity.logMessage("can't stop the server");
            e.printStackTrace();
        }
    }
}
