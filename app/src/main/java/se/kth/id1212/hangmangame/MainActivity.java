package se.kth.id1212.hangmangame;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUserInterface();
    }

    public void setupUserInterface() {

        nameField = (EditText) findViewById(R.id.nameField);
        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = nameField.getText().toString();
                if (playerName.isEmpty()) {
                    //Snackbar.make(findViewById(R.id.myMainLayout), "Please enter name",
                    //        Snackbar.LENGTH_SHORT)
                    //        .show();
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
