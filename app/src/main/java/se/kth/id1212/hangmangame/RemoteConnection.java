package se.kth.id1212.hangmangame;

import android.renderscript.RenderScript;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mellstrand on 2017-12-13.
 */

public class RemoteConnection implements Runnable {

    String playerName;
    protected final MessageListener ml;
    boolean receive = false;
    Socket socket;
    BufferedReader fromServer;
    PrintWriter toServer;

    public RemoteConnection(final MessageListener ml, String playerName) {
        this.ml = ml;
        this.playerName = playerName;
    }

    public void start() {
        connect();
        receive = true;
        ml.showMessage("TESTING");
        //new Thread(this).start();
    }

    @Override
        public void run() {
        try {
            transmit(playerName);
            while (receive) {
                ml.showMessage(fromServer.readLine());
            }
        } catch (IOException ioe) {
            receive = false;
            System.err.println("ERROR, RemoteConnection/run()");
        }
    }

    public void connect() {
        try {
            socket = new Socket(Constants.SERVER_NAME, Constants.SERVER_PORT);
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServer = new PrintWriter(socket.getOutputStream());
        } catch(Exception e) {
            System.err.println("ERROR, RemoteConnection/connect()");
        }
    }

    public void disconnect() {
        try {
            socket.close();
            socket = null;
        } catch(IOException ioe) {
            System.err.println("ERROR, RemoteConnection/disconnect()");
        }
    }

    public void transmit(String message) {
        toServer.println(message);
        toServer.flush();
    }

}
