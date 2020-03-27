package com.example.quizlet;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private String player = "", quiz = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        // Set the UI
        Button btnPlay = findViewById(R.id.btnPlay);
        editName = findViewById(R.id.editName);
        Spinner spinner = findViewById(R.id.spinChoices);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.txtChoices, R.layout.custom_spinner);

        // Get data from Bundle
        if(extras != null){
            player = extras.getString("user");
            editName.setText(player);
        }

        adapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quiz = position == 0 ? "pictures" : "strings";
//                Toast.makeText(getApplicationContext(), "You chose: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Add listener
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Quiz.class);
                Bundle extras = new Bundle();

                if(!TextUtils.isEmpty(editName.getText().toString()) && player != null) {
                    player = editName.getText().toString();
                    // pass the player name to the next screen
                    extras.putString("user", player);
                    extras.putString("quiz_type", quiz);
                    i.putExtras(extras);
                    startActivity(i);
                }
                else {
                    editName.setError("Please enter your name");
                }
            }
        });
    }
}
