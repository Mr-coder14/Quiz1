package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Question;

public class QuizLevelActivity extends AppCompatActivity {
    private ConstraintLayout easy, moderate, hard;
    private ArrayList<Question> questions;
    private ImageView imageButton;
    private TextView easyText, moderateText, hardText, easyQuizText, moderateQuizText, hardQuizText;
    private String category_name;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);

        // Initialize views
        easy = findViewById(R.id.easy);
        moderate = findViewById(R.id.moderate);
        hard = findViewById(R.id.hard);
        imageButton = findViewById(R.id.backpr);
        easyText = findViewById(R.id.easyText);
        moderateText = findViewById(R.id.moderateText);
        hardText = findViewById(R.id.hardText);
        easyQuizText = findViewById(R.id.easyQuizText);
        moderateQuizText = findViewById(R.id.moderateQuizText);
        hardQuizText = findViewById(R.id.hardQuizText);

        questions = new ArrayList<>();
        questions.clear();

        // Get category name from Intent
        category_name = getIntent().getStringExtra("name");

        // Update texts dynamically based on category_name
        if ("Web Development".equals(category_name)) {
            easyText.setText("HTML");
            moderateText.setText("CSS");
            hardText.setText("JavaScript");

            easyQuizText.setText("HTML Quiz");
            moderateQuizText.setText("CSS Quiz");
            hardQuizText.setText("JavaScript Quiz");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("quizzes").child(category_name);

        // Back button functionality
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Level selection click listeners
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuestions("Web Development".equals(category_name) ? "html_programming" : "easy");
            }
        });

        moderate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuestions("Web Development".equals(category_name) ? "css_programming" : "moderate");
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuestions("Web Development".equals(category_name) ? "javascript_programming" : "hard");
            }
        });
    }

    private void fetchQuestions(String level) {
        questions.clear();
        databaseReference.child(level).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Question question = dataSnapshot.getValue(Question.class);
                        questions.add(question);
                    }
                    if (!questions.isEmpty()) {
                        Intent intent = new Intent(QuizLevelActivity.this, QuizActivity.class);
                        intent.putParcelableArrayListExtra("questions", questions);
                        intent.putExtra("title", category_name);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizLevelActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}