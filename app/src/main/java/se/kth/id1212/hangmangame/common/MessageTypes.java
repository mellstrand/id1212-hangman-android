package se.kth.id1212.hangmangame.common;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13
 *
 * Enum MessageTypes for coherent message handling between client and server
 */

public enum MessageTypes {

    /**
     * To start a game for the first time
     */
    INIT,
    /**
     * Request a new word
     */
    NEW,
    /**
     * Make a guess
     */
    GUESS,
    /**
     * Game status message
     */
    STATUS,
    /**
     * Leaving the game server
     */
    END
}
