package se.kth.id1212.hangmangame.net;

import java.io.BufferedReader;
import java.io.IOException;

import se.kth.id1212.hangmangame.common.ServerMessage;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13.
 */

public class MessageListener extends Thread {

    private final ServerMessage serverMessage;
    private BufferedReader fromServer;
    private volatile boolean receive = false;

    public MessageListener(final ServerMessage serverMessage, BufferedReader fromServer) {
        this.serverMessage = serverMessage;
        this.fromServer = fromServer;
        receive = true;
    }

    @Override
    public void run() {
        try {
            while (receive && !isInterrupted()) {
                deliver(fromServer.readLine());
            }
            if(isInterrupted()) {
                shutdown();
            }
        } catch (IOException ioe) {
            receive = false;
            System.err.println("ERROR; MessageListener.run()" + ioe);
        }
    }

    public void shutdown() {
        receive = false;
    }

    /**
     * @param message Content to be sent to GameActivity
     */
    private void deliver(String message) {
        if(receive) {
            serverMessage.handleMessage(message);
        }
    }

}
