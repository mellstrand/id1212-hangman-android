package se.kth.id1212.hangmangame.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringJoiner;

import se.kth.id1212.hangmangame.R;
import se.kth.id1212.hangmangame.common.Constants;
import se.kth.id1212.hangmangame.common.MessageTypes;
import se.kth.id1212.hangmangame.common.ServerMessage;
import se.kth.id1212.hangmangame.net.MessageListener;
import se.kth.id1212.hangmangame.net.ServerConnection;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13
 *
 * Activity for the actual game playing sequences
 */
public class GameActivity extends AppCompatActivity implements ServerMessage {

    private ServerConnection serverConnection;
    private MessageListener messageListener;
    private Button initGameButton;
    private Button sendGuessButton;
    private Button newWordButton;
    private Button endGameButton;
    private TextView guessTextView;
    private TextView remainingTextView;
    private TextView scoreTextView;
    private EditText sendGuessField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupGameInterface();
        connect();

    }

    /**
     * Retrieve player name and then make a connection to the server
     */
    private void connect() {
        Intent intent = getIntent();
        String playerName = intent.getExtras().getString("name");
        new ConnectServer().execute(playerName);
    }

    /**
     * Fixing the UI elements and adding listener to corresponding buttons
     */
    private void setupGameInterface() {
        initGameButton = findViewById(R.id.initGameButton);
        newWordButton = findViewById(R.id.newWordButton);
        endGameButton = findViewById(R.id.endGameButton);
        sendGuessField = findViewById(R.id.sendGuessField);
        sendGuessButton = findViewById(R.id.sendGuessButton);
        guessTextView = findViewById(R.id.guessEdit);
        remainingTextView = findViewById(R.id.remainingEdit);
        scoreTextView = findViewById(R.id.scoreEdit);

        newWordButton.setVisibility(View.GONE);
        endGameButton.setVisibility(View.GONE);

        initGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new sendServerMessage().execute(MessageTypes.INIT.toString());

                initGameButton.setVisibility(View.GONE);
                newWordButton.setVisibility(View.VISIBLE);
                endGameButton.setVisibility(View.VISIBLE);
                sendGuessField.setVisibility(View.VISIBLE);
                sendGuessButton.setVisibility(View.VISIBLE);

            }
        });

        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new sendServerMessage().execute(MessageTypes.NEW.toString());
            }
        });

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageTypes type = MessageTypes.END;
                new sendServerMessage().execute(type.toString());
                closeActivity(Constants.GAME_FINISHED);
            }
        });

        sendGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringJoiner joiner = new StringJoiner(Constants.TCP_DELIMITER);
                joiner.add(MessageTypes.GUESS.toString());
                joiner.add(sendGuessField.getText().toString());
                new sendServerMessage().execute(joiner.toString());
                sendGuessField.getText().clear();
                sendGuessButton.setEnabled(false);
            }
        });

    }

    /**
     * Receive messages from MessageListener who receives it from the server
     * MessageListener runs on another thread, therefore switch to UI thread.
     * Sends to 'setMessage' if a status message from the server
     * Otherwise show message for "debug" purpose.
     *
     * @param message Message received from game server
     */
    public void handleMessage(final String message) {
        final String[] tokens = message.split(Constants.TCP_DELIMITER);
        runOnUiThread(new Runnable() {
            public void run() {
                String inputType = tokens[0];
                String statusType = MessageTypes.STATUS.toString();
                if (inputType.equals(statusType)) {
                    setMessage(tokens);
                } else {
                    Toast.makeText(GameActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Handles the status message from the server and updates the UI to the user
     * A lot of splits, could be changed at server side (better message),
     * or UI side (Another setup of TextViews)
     * @param messages Status message from the game server
     */
    private void setMessage(final String... messages) {
        String[] guess = messages[1].split(":");
        String guessFixed = guess[1].replace("", " ").trim();
        guessTextView.setText(guessFixed);
        String[] remain = messages[2].split(":");
        remainingTextView.setText(remain[1]);
        String[] score = messages[3].split(":");
        scoreTextView.setText(score[1]);
        sendGuessButton.setEnabled(true);
    }

    /**
     * A method to close the activity and it's started threads, sockets, etc.
     * @param status Exit status, e.g. FINISHED, ERROR, or...
     */
    private void closeActivity(int status) {
        messageListener.shutdown();
        serverConnection.disconnect();
        GameActivity.this.setResult(status);
        GameActivity.this.finish();
    }

    /**
     * AsyncTask to connect to the server
     * After establishment, start the MessageListener
     */
    @SuppressLint("StaticFieldLeak")
    private class ConnectServer extends AsyncTask<String, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(String... strings) {
            ServerConnection serverConnection = new ServerConnection(strings[0]);
            serverConnection.connect();
            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            GameActivity.this.serverConnection = serverConnection;
            GameActivity.this.messageListener = new MessageListener(GameActivity.this, serverConnection.getFromServer());
            new Thread(messageListener).start();
            Toast.makeText(GameActivity.this, R.string.connect_init, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * AsyncTask for sending messages to the server
     */
    @SuppressLint("StaticFieldLeak")
    private class sendServerMessage extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            GameActivity.this.serverConnection.transmit(strings[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            Toast.makeText(GameActivity.this, R.string.message_sent, Toast.LENGTH_LONG).show();
        }
    }

}
