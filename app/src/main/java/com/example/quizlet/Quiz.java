package com.example.quizlet;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.view.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Quiz extends AppCompatActivity {

    private TextView tvProgress;
    private TextView tvName;
    private TextView tvQuestion;
    private ImageView imgView;
    private RadioGroup rbtnChoices;
    private RadioButton rbtnChoice1;
    private RadioButton rbtnChoice2;
    private RadioButton rbtnChoice3;
    private RadioButton rbtnChoice4;
    private RadioButton rbtnSelected;
    private int quizCtr = 0;
    private int score =0;
    private HashMap<String, String> map = new HashMap<String, String>();
    private ArrayList<String> listTerm = new ArrayList<String>();
    private ArrayList<String> listDef = new ArrayList<String>();
    private String strCorrect = "";
    private String player = "";
    private String strTerm = "";
    private String quiz_type = "pictures";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Bundle extras = getIntent().getExtras();

        // Setup UI
        Button btnSubmit = findViewById(R.id.btnSubmit);
        FloatingActionButton floatQuit = findViewById(R.id.floatQuit);
        tvName = findViewById(R.id.tvName);
        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        imgView = findViewById(R.id.imgView);
        rbtnChoice1 = findViewById(R.id.rbtnChoice1);
        rbtnChoice2 = findViewById(R.id.rbtnChoice2);
        rbtnChoice3 = findViewById(R.id.rbtnChoice3);
        rbtnChoice4 = findViewById(R.id.rbtnChoice4);
        rbtnChoices = findViewById(R.id.rbtnChoices);

        // Get data from Bundle
        if(extras != null){
            player = extras.getString("user");
            tvName.setText("Hello " + player + "!");
            quiz_type = extras.getString("quiz_type");
        }

        readQuizData();
        // setup progress counter
        tvProgress.setText(" Progress: " + quizCtr + "/" + map.size());
//        for (String key : map.keySet()) {
//            System.out.println(key + ":" + map.get(key)); // just for checking what's in the map
//        }
//        for(int i = 0; i < listDef.size(); i++) {
//            System.out.println("Image: " + listTerm.get(i) + " | Meaning: " + listDef.get(i));
//        }
//        System.out.println(map.size());
        if(listTerm.size() != 0) {
            createQuiz();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = rbtnChoices.getCheckedRadioButtonId();
                strCorrect = quiz_type.equals("pictures") ? strCorrect : strTerm;

                if(selected != -1) {    // makes sure that a radio button is selected

                    rbtnSelected = findViewById(selected);
                    rbtnSelected.getText();

                    // checks if the selected option is the correct answer
                    if (strCorrect == rbtnSelected.getText() && score <= 20) {
                        score++;
                        Toast.makeText(getApplicationContext(), "Correct! Your current score: " + score, Toast.LENGTH_LONG).show();
                    }

                    if (listTerm.size() != 0) {    // makes sure that the list of images has not been exhausted
                        createQuiz();   // call to create quiz
                    }

                    quizCtr++;
                    tvProgress.setText(" Progress: " + quizCtr + "/" + map.size()); // progress tracker

                    if (quizCtr == 20) {
                        stopQuiz(); // quiz is over
                    }
                }
            }
        });

        floatQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopQuiz();
            }
        });
    }

    // function that will abort the quiz and show the score
    private void stopQuiz() {
        // go to next screen
        Intent i = new Intent(Quiz.this, Score.class);
        Bundle extras = new Bundle();

        // pass the player name to the next screen
        extras.putString("user", player);

        // pass the score to the next screen
        String strScore = score + "/" + map.size();
        extras.putString("score", strScore);

        i.putExtras(extras);
        startActivity(i);
    }

    // function that sets up the quiz
    private void createQuiz() {
        // shuffle terms (or images) and definitions
        Collections.shuffle(listTerm);
        Collections.shuffle(listDef);

        // deselect radio buttons
        rbtnChoices.clearCheck();

        // initialize locals
        ArrayList<String> listAnswers = new ArrayList<String>();

        strTerm = listTerm.get(0);  // get first term / image from list
        listTerm.remove(0);    // remove first term / image from list

        // get definition of image from hashmap
        strCorrect = map.get(strTerm);

        if(quiz_type.equals("pictures")) {
            listAnswers.add(strCorrect);    // add to answer list
            // add 3 more to answer list except correct answer
            int ctr = 0;
            for (int i = 0; i < listDef.size(); i++) {
                if (!listDef.get(i).equals(strCorrect) && ctr < 3) {
                    listAnswers.add(listDef.get(i));
                    ctr++;
                }
            }
        }
        else {
            listAnswers.add(strTerm);
            // add 3 more to answer list except correct answer
            int ctr = 0;
            Object randomAns;
            while (ctr < 3) {
                randomAns = map.keySet().toArray()[new Random().nextInt(map.keySet().toArray().length)];
                if(!listAnswers.contains(randomAns)) {
                    listAnswers.add(randomAns.toString());
                    ctr++;
                }
            }
        }
        Collections.shuffle(listAnswers);   // shuffle the list

        // set the choices to screen
        rbtnChoice1.setText(listAnswers.get(0));
        rbtnChoice2.setText(listAnswers.get(1));
        rbtnChoice3.setText(listAnswers.get(2));
        rbtnChoice4.setText(listAnswers.get(3));

        if(quiz_type.equals("pictures")) {
            // get resource id from string
            // src: https://android.okhelp.cz/get-resource-id-by-resources-string-name-android-example/
            int resID = getResources().getIdentifier(strTerm, "drawable", getPackageName());

            // set image using resource ID
            imgView.setImageResource(resID);
        }
        else {
            imgView.getLayoutParams().height = 10;
            tvQuestion.setAllCaps(false);
            tvQuestion.setText(strCorrect);
        }
    }

    // function that reads the quiz data from raw data (csv)
    private void readQuizData() {

        InputStream inputStream = getResources().openRawResource(R.raw.whatisthissign);
        if(quiz_type.equals("strings")) {  // change resource if quiz_type is not pictures
            inputStream = getResources().openRawResource(R.raw.quiz3);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        String line = "";

        while (true) {
            try {
                // read data per line
                if (!((line = reader.readLine()) != null)) break;
                // Split by ","
                String[] parts = line.split(",", 2);

                // put into hashmap
                String key = parts[0];
                String value = parts[1].replaceAll("^\"|\"$", "");
                map.put(key, value);

                // put into array list
                listTerm.add(key);     // terms / image list
                listDef.add(value);     // definition list

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("QuizBee", e.toString());
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("QuizBee", e.toString());
        }

    }
}
