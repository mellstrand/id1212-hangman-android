package se.kth.id1212.hangmangame.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import se.kth.id1212.hangmangame.view.GameActivity;
import se.kth.id1212.hangmangame.common.ServerMessage;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13.
 */

public class ServerConnection {

    private GameActivity gameActivity;
    private String playerName;
    protected final ServerMessage serverMessage;
    private boolean receive = false;
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public ServerConnection(final ServerMessage serverMessage, String playerName) {
        this.serverMessage = serverMessage;
        this.playerName = playerName;
    }
/*
    public void run() {
        try {
            transmit(playerName);
            while (receive) {
                fromServer.readLine();
            }
        } catch (IOException ioe) {
            receive = false;
            System.err.println("ERROR, RemoteConnection/run()" + ioe);
        }
    }
*/
    public void connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("10.0.2.2", 5000));
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServer = new PrintWriter(socket.getOutputStream());

            //new Thread(new MessageListener(serverMessage, fromServer)).start();

            transmit(playerName);

        } catch(Exception e) {
            System.err.println("ERROR, RemoteConnection/connect()" + e);
        }
    }

    public BufferedReader getFromServer() {
        return fromServer;
    }

    public void disconnect() {
        try {
            socket.close();
            socket = null;
        } catch(IOException ioe) {
            System.err.println("ERROR, RemoteConnection/disconnect()" + ioe);
        }
    }

    public void transmit(String message) {
        toServer.println(message);
        toServer.flush();
    }

}
