package se.kth.id1212.hangmangame.common;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13
 *
 * Constants that can be used
 */

public class Constants {

    /**
     * Special alias to host loopback interface on emulator machine
     */
    public final static String SERVER_NAME = "10.0.2.2";
    /**
     * Servers port number
     */
    public final static int SERVER_PORT = 5000;
    /**
     * Message parts separator
     */
    public final static String TCP_DELIMITER = "##";
    /**
     * RequestCode for 'startActivityForResult()'
     */
    public static final int GAME_ACTIVITY = 1;
    /**
     * ResponseCode for 'onActivityResult'
     */
    public static final int GAME_FINISHED = 2;


}
