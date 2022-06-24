package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewTimer;
    private TextView textViewScore;
    private TextView textViewOpinion0;
    private TextView textViewOpinion1;
    private TextView textViewOpinion2;
    private TextView textViewOpinion3;
    private ArrayList<TextView> options = new ArrayList<>();

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;
    private int countOfQuestions = 0;
    private int countOfRightAnswers =0;
    private int currentCountOfRightAnswers =0;
    private long currentMillisUntilFinished;
    private boolean gameOver = false;
    private long timeOfGame = 15000;
    private CountDownTimer timer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        textViewOpinion0 = findViewById(R.id.textViewOpinion0);
        textViewOpinion1 = findViewById(R.id.textViewOpinion1);
        textViewOpinion2 = findViewById(R.id.textViewOpinion2);
        textViewOpinion3 = findViewById(R.id.textViewOpinion3);
        options.add(textViewOpinion0);
        options.add(textViewOpinion1);
        options.add(textViewOpinion2);
        options.add(textViewOpinion3);
        playNext();
        startTimer(timeOfGame);
//        textViewTimer = findViewById(R.id.textViewTimer);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.edit().putInt("test",5).apply();
//        int test = preferences.getInt("test",5);
//        Toast.makeText(this,Integer.toString(test),Toast.LENGTH_SHORT).show();
//        CountDownTimer timer = new CountDownTimer(6000,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                int seconds = (int)(millisUntilFinished/1000);
//                seconds++;
//                textViewTimer.setText(Integer.toString(seconds));
//            }
//
//            @Override
//            public void onFinish() {
//                Toast.makeText(MainActivity.this,"Таймер завершен",Toast.LENGTH_SHORT).show();
//                textViewTimer.setText("0");
//            }
//        };
//        timer.start();
    }




    private void playNext() {
        generateQuestion();
        for (int i = 0; i < options.size(); i++) {
            if (i == rightAnswerPosition) {
                options.get(i).setText(Integer.toString(rightAnswer));
            } else {
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
                ;
            }
        }
        String score = String.format("%s / %s",countOfRightAnswers,countOfQuestions);
        textViewScore.setText(score);

    }

    private void generateQuestion() {
        int a = (int) (Math.random()*(max-min+1)+min);
        int b = (int) (Math.random()*(max-min+1)+min);
        int mark = (int) (Math.random()*2);
        isPositive = mark==1;
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s + %s",a,b);
        } else  {
            rightAnswer = a - b;
            question = String.format("%s - %s",a,b);
        }
        textViewQuestion.setText(question);
        rightAnswerPosition = (int) (Math.random()*4);
    }

    private int generateWrongAnswer() {
        int result;
        do {
             result = (int) (Math.random()*max*2+1)-max-min;
        } while (result==rightAnswer);

      return result;
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
    }


    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();

                countOfRightAnswers++;
                addTimeForRightAnswer();
            } else {
                Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show();
                removeTimeForRightAnswer();
            }
            countOfQuestions++;
            playNext();
        }
    }

    private void addTimeForRightAnswer() {
        timer.cancel();
        startTimer(currentMillisUntilFinished+2000);
        textViewScore.setBackgroundResource(android.R.color.holo_green_light);
    }

    private void removeTimeForRightAnswer() {
        timer.cancel();
        startTimer(currentMillisUntilFinished-2000);
        textViewScore.setBackgroundResource(android.R.color.holo_red_light);
    }

    private void startTimer(long timeOfGame) {
        timer = new CountDownTimer(timeOfGame, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentMillisUntilFinished = millisUntilFinished;
                System.out.println(currentMillisUntilFinished);
                textViewTimer.setText(getTime(millisUntilFinished));
                if (millisUntilFinished < 5000) {
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                } else
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }

                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }

        };
        timer.start();
    }
}