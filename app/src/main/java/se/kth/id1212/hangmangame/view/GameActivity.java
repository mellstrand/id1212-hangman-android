package se.kth.id1212.hangmangame.view;

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

    String playerName;
    private ServerConnection serverConnection;

    private Button initGameButton;
    private Button sendGuessButton;
    private Button newWordButton;
    private Button endGameButton;
    private TextView guessTextView;
    private TextView remainingTextView;
    private TextView scoreTextView;
    private EditText guessField;

    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        playerName = intent.getExtras().getString("name");
        setupGameInterface();
        connect();

    }

    public void connect() {
        new ConnectServer().execute();
    }

    public void setupGameInterface() {
        initGameButton = (Button)findViewById(R.id.initGameButton);
        sendGuessButton = (Button) findViewById(R.id.sendGuessButton);
        newWordButton = (Button)findViewById(R.id.newWordButton);
        endGameButton = (Button) findViewById(R.id.endGameButton);
        guessTextView = (TextView) findViewById(R.id.guessEdit);
        remainingTextView = (TextView) findViewById(R.id.remainingEdit);
        scoreTextView = (TextView) findViewById(R.id.scoreEdit);

        newWordButton.setVisibility(View.GONE);
        endGameButton.setVisibility(View.GONE);

        guessField = (EditText) findViewById(R.id.guessField);

        initGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGameButton.setVisibility(View.GONE);
                newWordButton.setVisibility(View.VISIBLE);
                endGameButton.setVisibility(View.VISIBLE);
                MessageTypes type = MessageTypes.INIT;
                new sendServerMessage().execute(type.toString());
            }
        });

        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageTypes type = MessageTypes.NEW;
                new sendServerMessage().execute(type.toString());
            }
        });

//TODO CLOSE ACTIVITY
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageTypes type = MessageTypes.END;
                new sendServerMessage().execute(type.toString());
            }
        });

        sendGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringJoiner joiner = new StringJoiner(Constants.TCP_DELIMETER);
                joiner.add(MessageTypes.GUESS.toString());
                joiner.add(guessTextView.getText().toString());
                new sendServerMessage().execute(joiner.toString());
            }
        });

    }

    public void handleMessage(String message) {
        final String[] tokens = message.split(Constants.TCP_DELIMETER);
        runOnUiThread(new Runnable() {
            public void run(){
                showMessage(tokens[1]);
            }
        });
    }
    //TODO FIX GUI AND MESSAGE STRINGS
    public void showMessage(final String message) {
        //String[] token = message.split(Constants.TCP_DELIMETER);
        //guessTextView.setText(token[0]);
        //guessTextView.setText(token[1]);
        //guessTextView.setText(token[2]);
        guessTextView.setText(message);
        sendGuessButton.setEnabled(true);
    }

    private class ConnectServer extends AsyncTask<Void, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(Void... voids) {

            ServerConnection serverConnection = new ServerConnection(GameActivity.this, playerName);
            serverConnection.connect();

            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            GameActivity.this.serverConnection = serverConnection;
            new Thread(new MessageListener(GameActivity.this, serverConnection.getFromServer())).start();
            Toast.makeText(GameActivity.this, "Connected, press 'Init Game' to play", Toast.LENGTH_LONG).show();
        }
    }

    private class sendServerMessage extends AsyncTask<String, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(String... strings) {

            String message = strings[0].toString();
            GameActivity.this.serverConnection.transmit(message);

            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            Toast.makeText(GameActivity.this, "Message sent", Toast.LENGTH_LONG).show();
        }
    }
}
