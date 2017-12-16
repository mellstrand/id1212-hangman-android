package se.kth.id1212.hangmangame.common;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-15.
 *
 * Interface for easier message deliveries
 */

public interface ServerMessage {

    /**
     * Message delivering method
     * @param serverMessage Message to be delivered
     */
    void handleMessage(String serverMessage);
}
