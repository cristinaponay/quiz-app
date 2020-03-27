package com.example.quizlet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Score extends AppCompatActivity {

    private TextView tvHello;
    private TextView tvScore;
    private String player = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Bundle extras = getIntent().getExtras();

        // Setup UI
        tvHello = findViewById(R.id.tvHello);
        tvScore = findViewById(R.id.tvScore);
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);

        // Get data from Bundle
        if(extras != null){
            player = extras.getString("user");
            tvHello.setText("Hey " + player + "!");

            String score = extras.getString("score");
            tvScore.setText("You scored: " + score);
        }

        // Gives player the option to play again
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Score.this, MainActivity.class);
                Bundle extras = new Bundle();

                // pass the player name to the next screen
                extras.putString("user", player);
                i.putExtras(extras);
                startActivity(i);
            }
        });
    }
}
