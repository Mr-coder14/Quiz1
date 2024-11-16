package com.RapCodeTechnologies.Quiz;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Fragments.QuizQuestionFragment;
import Fragments.ResultFragment;
import Models.Question;

public class QuizActivity extends AppCompatActivity {
    private DatabaseReference quizDatabase;
    private List<Question> questionList = new ArrayList<>();
    private CountDownTimer countDownTimer;
    private static final int QUESTION_TIME = 30000; // 30 seconds in milliseconds
    private static final int TIMER_INTERVAL = 1000;
    private int currentQuestionIndex = 0;
    private TextView quiztitle,timer;
    private ProgressBar quizprogress;
    private ImageButton imageButton;
    private int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        quiztitle=findViewById(R.id.quiztitile);
        timer=findViewById(R.id.timer);
        quizprogress=findViewById(R.id.progressquiz);
        imageButton=findViewById(R.id.backquiz);
        quizDatabase = FirebaseDatabase.getInstance().getReference("quiz_questions");
        loadQuestionsFromFirebase();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        }
    private void loadQuestionsFromFirebase() {
        quizDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    questionList.add(question);
                }
                if (!questionList.isEmpty()) {
                    loadNextQuestion();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel the timer when activity is destroyed
        }
    }

    public void loadNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            findViewById(R.id.timer).setVisibility(View.VISIBLE);
            findViewById(R.id.progressquiz).setVisibility(View.VISIBLE);
            findViewById(R.id.backquiz).setVisibility(View.VISIBLE);
            findViewById(R.id.quiztitile).setVisibility(View.VISIBLE);
            findViewById(R.id.timeriamge).setVisibility(View.VISIBLE);
            // Load the next question fragment
            Question question = questionList.get(currentQuestionIndex);
            QuizQuestionFragment fragment = QuizQuestionFragment.newInstance(
                    question.getQuestion(),
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD(),
                    question.getCorrectAnswer()
            );
            fragment.setOnCorrectAnswerListener(() -> score++);
            quizprogress.setProgress((currentQuestionIndex + 1) * 100 / questionList.size());

            // Start timer for the current question
            startTimer();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerquiz, fragment);
            transaction.commit();

            currentQuestionIndex++;
        } else {
            // All questions have been answered, load the ResultFragment to display the score
            showResultFragment();
        }
    }
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel any existing timer
        }

        countDownTimer = new CountDownTimer(QUESTION_TIME, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer text
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Time is up, load the next question
                loadNextQuestion();
            }
        };

        countDownTimer.start();
    }

    private void showResultFragment() {
        findViewById(R.id.timer).setVisibility(View.GONE);
        findViewById(R.id.progressquiz).setVisibility(View.GONE);
        findViewById(R.id.quiztitile).setVisibility(View.GONE);
        findViewById(R.id.timeriamge).setVisibility(View.GONE);
        findViewById(R.id.backquiz).setVisibility(View.GONE);
        ResultFragment resultFragment = ResultFragment.newInstance(score, questionList.size());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerquiz, resultFragment);
        transaction.commit();
    }
    }
