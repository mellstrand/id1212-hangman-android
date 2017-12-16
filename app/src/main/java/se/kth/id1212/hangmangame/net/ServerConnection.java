package se.kth.id1212.hangmangame.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import se.kth.id1212.hangmangame.common.Constants;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13
 *
 * Class to handle the remote connection to a server
 */

public class ServerConnection {

    private final String playerName;
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public ServerConnection(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Create and open socket and retrieve pipes for reading and sending messages
     */
    public void connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(Constants.SERVER_NAME, Constants.SERVER_PORT));
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServer = new PrintWriter(socket.getOutputStream());

            transmit(playerName);

        } catch(Exception e) {
            System.err.println("ERROR, RemoteConnection/connect()" + e);
        }
    }

    /**
     * Getter for the reading pipe
     * @return Reading pipe
     */
    public BufferedReader getFromServer() {
        return fromServer;
    }

    /**
     * Close the socket to the remote server
     */
    public void disconnect() {
        try {
            socket.close();
            socket = null;
        } catch(IOException ioe) {
            System.err.println("ERROR, RemoteConnection/disconnect()" + ioe);
        }
    }

    /**
     * Send a message to the remote server
     * @param message Content to be sent
     */
    public void transmit(String message) {
        toServer.println(message);
        toServer.flush();
    }

}
