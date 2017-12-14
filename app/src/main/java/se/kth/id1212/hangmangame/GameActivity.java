package se.kth.id1212.hangmangame;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by mellstrand on 2017-12-13.
 */

public class GameActivity extends AppCompatActivity {

    String playerName;
    boolean connected;
    private RemoteConnection remoteConnection;

    private Button sendGuessButton;
    private Button newWordButton;
    private Button endGameButton;
    private TextView messageTextView;
    private EditText guessField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //Intent intent = getIntent();
        //playerName = intent.getExtras().getString("name");
        //setupGameInterface();
    }

    public void setupGameInterface() {
        sendGuessButton = (Button) findViewById(R.id.sendGuessButton);
        newWordButton = (Button)findViewById(R.id.newWordButton);
        endGameButton = (Button) findViewById(R.id.endGameButton);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        guessField = (EditText) findViewById(R.id.guessField);

        showMessage(playerName);
    }

    public void showMessage(final String message) {
        messageTextView.setText(message);
        sendGuessButton.setEnabled(true);
    }

}
