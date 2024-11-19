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

import com.google.firebase.auth.FirebaseAuth;
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
    private static final int QUESTION_TIME = 30000;
    private static final int TIMER_INTERVAL = 1000;
    private int currentQuestionIndex = 0;
    private String userid;
    private TextView quiztitle,timer,questionCount;
    private ProgressBar quizprogress;
    private ImageButton imageButton;
    private int score = 0;
    private ProgressBar progressBar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        questionCount = findViewById(R.id.questioncount);
        quiztitle=findViewById(R.id.quiztitile);
        timer=findViewById(R.id.timer);
        quizprogress=findViewById(R.id.progressquiz);
        userid= FirebaseAuth.getInstance().getUid();
        progressBar1=findViewById(R.id.progressbaroverall);
        imageButton=findViewById(R.id.backquiz);
        quizDatabase = FirebaseDatabase.getInstance().getReference("quiz_questions");
        loadQuestionsFromFirebase();
        progressBar1.setVisibility(View.VISIBLE);
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

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void loadNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
            question.setQuestionIndex(currentQuestionIndex);

            QuizQuestionFragment fragment = QuizQuestionFragment.newInstance(
                    question.getQuestion(),
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD(),
                    question.getCorrectAnswer(),
                    question.getQuestionIndex()
            );

            fragment.setOnImageLoadListener(() -> {

                fragment.setOnCorrectAnswerListener(() -> score++);
                quizprogress.setProgress((currentQuestionIndex - 1) * 100 / questionList.size());
                startTimer();
            });

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerquiz, fragment);
            transaction.commit();

            currentQuestionIndex++;
        } else {

            showResultFragment();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(QUESTION_TIME, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

                timer.setText(String.valueOf(millisUntilFinished / 1000  + "s"));
            }

            @Override
            public void onFinish() {

                loadNextQuestion();
            }
        };

        countDownTimer.start();
    }

    private void showResultFragment() {

        findViewById(R.id.timer).setVisibility(View.GONE);
        progressBar1.setVisibility(View.GONE);
        findViewById(R.id.progressquiz).setVisibility(View.GONE);
        findViewById(R.id.quiztitile).setVisibility(View.GONE);
        findViewById(R.id.timeriamge).setVisibility(View.GONE);
        findViewById(R.id.layouttt).setVisibility(View.GONE);
        findViewById(R.id.questioncount).setVisibility(View.GONE);
        findViewById(R.id.backquiz).setVisibility(View.GONE);


        ResultFragment resultFragment = ResultFragment.newInstance(score, questionList.size(), userid);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerquiz, resultFragment);
        transaction.commit();
    }

}
