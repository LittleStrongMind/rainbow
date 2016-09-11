package little.rainbow;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
RainbowServerRunnableUDP est le ServerUDP
**/
public class RainbowServerRunnableUDP implements Runnable {

    private RainbowMenuActivity activity;
    private DatagramSocket serverSocket;

    private DatagramPacket recvPacket;
    private InetAddress IPAddress;

    private byte[] recvData;
    private byte[] sendData;

    private int dataRecv;
    public RainbowServerRunnableUDP(RainbowMenuActivity activity){
        this.activity = activity;

        recvData = new byte[65507];
        sendData = new byte[65507];

    }

    public String recv() throws IOException {

        serverSocket.receive(recvPacket);

        String message = new String( recvPacket.getData());
        Log.d("MESSAGE", "RECEIVED :: " + message);
        return message;
    }

    public void send(String message) throws IOException {

        if(recvPacket.getPort() < 0 || recvPacket.getPort() > 65535){
            Log.d("DEBUG", "ERROR");
            return;
        }

        int port = recvPacket.getPort();
        sendData = message.getBytes();
        IPAddress = recvPacket.getAddress();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

        serverSocket.send(sendPacket);
        Log.d("MESSAGE", "SENT");

    }

    public void stop(){
        activity.logMessage("UDP SERVER STOPPED");
        Log.d("DEBUG", "UDP SERVER STOPPED");

        if(serverSocket != null){
            serverSocket.close();
        }
    }
    @Override
    public void run() {

        try {

            serverSocket = new DatagramSocket(RainbowCoreActivity.UDP_PORT);
            recvPacket = new DatagramPacket(recvData, recvData.length);


            activity.logMessage("UDP READY localhost:" + RainbowCoreActivity.UDP_PORT);
            Log.d("DEBUG", "UDP READY localhost:" + RainbowCoreActivity.UDP_PORT);
            // on bloque le serveur tant que l'on n'a pas reçu de message du client


            // On spécifie au client qu'il peut commencer l'envoi
            send("HELLO CLIENT\n");
            activity.logMessage("UDP CLIENT COMFIRMATION SENT");
            Log.d("DEBUG", "UDP CLIENT COMFIRMATION SENT");


            Thread recvThread = new Thread(new RecvRunnable());
            Thread sendThread = new Thread(new SendRunnable());


            sendThread.start();
            recvThread.start();

            recvThread.join();
            sendThread.join();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * se charge de la réception des frames
     */
    class RecvRunnable implements Runnable{

        @Override
        public void run() {
            try {
                activity.logMessage("START RECEIVING");
                Log.d("DEBUG", "START RECEIVING");

                while(true) {
                    recv();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * se charge de l'envoie des données gyroscope
     */
    class SendRunnable implements Runnable{

        @Override
        public void run() {
            try {
                activity.logMessage("START SENDING");
                Log.d("DEBUG", "START SENDING");

                while(true){

                    send(activity.getGyr());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
