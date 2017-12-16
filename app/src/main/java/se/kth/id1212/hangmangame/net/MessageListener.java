package se.kth.id1212.hangmangame.net;

import java.io.BufferedReader;
import java.io.IOException;

import se.kth.id1212.hangmangame.common.ServerMessage;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13
 *
 * A listener for messages from a server that runs on another thread to not block UI thread
 * or any other activity
 */

public class MessageListener extends Thread {

    private final ServerMessage serverMessage;
    private final BufferedReader fromServer;
    private volatile boolean receive = false;

    public MessageListener(final ServerMessage serverMessage, BufferedReader fromServer) {
        this.serverMessage = serverMessage;
        this.fromServer = fromServer;
        receive = true;
    }

    /**
     * Waiting for messages from the server
     */
    @Override
    public void run() {
        try {
            while (receive) {
                deliver(fromServer.readLine());
            }
        } catch (IOException ioe) {
            receive = false;
            System.err.println("ERROR; MessageListener.run()" + ioe);
        }
    }

    /**
     * Change state to not retrieve any more messages
     */
    public void shutdown() {
        receive = false;
    }

    /**
     * A helper method for deliver message back to GameActivity
     * @param message Content to be sent to GameActivity
     */
    private void deliver(String message) {
        if(receive) {
            serverMessage.handleMessage(message);
        }
    }

}
