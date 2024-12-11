package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Question;

public class QuizLevelActivity extends AppCompatActivity {
    private ConstraintLayout easy,moderate,hard;
    private ArrayList<Question> questions;
    private ImageView imageButton;
    private String category_name;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);
        easy=findViewById(R.id.easy);
        moderate=findViewById(R.id.moderate);
        hard=findViewById(R.id.hard);
        imageButton=findViewById(R.id.backpr);
        questions=new ArrayList<>();
        questions.clear();
        category_name=getIntent().getStringExtra("name");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("quizzes").child(category_name);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchquestions("easy");
            }
        });

        moderate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchquestions("moderate");
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchquestions("hard");

            }
        });
    }

    private void fetchquestions(String level) {
        questions.clear();
        databaseReference.child(level).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Question question=dataSnapshot.getValue(Question.class);
                        questions.add(question);
                    }
                    if(!questions.isEmpty()){
                        Intent intent = new Intent(QuizLevelActivity.this, QuizActivity.class);
                        intent.putParcelableArrayListExtra("questions", questions);
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