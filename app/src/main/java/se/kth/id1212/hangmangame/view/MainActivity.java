package se.kth.id1212.hangmangame.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import se.kth.id1212.hangmangame.R;

/**
 * @author Tobias Mellstrand
 * @date 2017-12-13.
 */

public class MainActivity extends AppCompatActivity {

    private Button connectButton;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUserInterface();
    }

    public void setupUserInterface() {

        nameField = (EditText) findViewById(R.id.nameField);
        connectButton = (Button) findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = nameField.getText().toString();
                if (playerName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please type a name", Toast.LENGTH_LONG).show();

                } else {
                    startGame(playerName);
                }
            }
        });
    }

    public void startGame(String playerName) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("name", playerName);
        startActivity(intent);

    }

}
