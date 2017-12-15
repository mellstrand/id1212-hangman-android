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
 * @date 2017-12-13.
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

    public void connect() {
        Intent intent = getIntent();
        String playerName = intent.getExtras().getString("name");
        new ConnectServer().execute(playerName);
    }

    public void setupGameInterface() {
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
                StringJoiner joiner = new StringJoiner(Constants.TCP_DELIMETER);
                joiner.add(MessageTypes.GUESS.toString());
                joiner.add(sendGuessField.getText().toString());
                new sendServerMessage().execute(joiner.toString());
                sendGuessField.getText().clear();
                sendGuessButton.setEnabled(false);
            }
        });

    }

    public void handleMessage(final String message) {
        final String[] tokens = message.split(Constants.TCP_DELIMETER);
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

    //TODO FIX GUI AND MESSAGE STRINGS
    public void setMessage(final String... messages) {
        String[] guess = messages[1].split(":");
        String guessFixed = guess[1].replace("", " ").trim();
        guessTextView.setText(guessFixed);
        String[] remain = messages[2].split(":");
        remainingTextView.setText(remain[1]);
        String[] score = messages[3].split(":");
        scoreTextView.setText(score[1]);
        sendGuessButton.setEnabled(true);
    }

    private void closeActivity(int status) {
        messageListener.shutdown();
        serverConnection.disconnect();
        GameActivity.this.setResult(status);
        GameActivity.this.finish();
    }

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
            Toast.makeText(GameActivity.this, "Connected, press 'Init Game' to play", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class sendServerMessage extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            GameActivity.this.serverConnection.transmit(strings[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            Toast.makeText(GameActivity.this, "Message sent", Toast.LENGTH_LONG).show();
        }
    }

}
