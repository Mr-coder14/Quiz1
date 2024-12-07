package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Adaptors.CategoryAdapter;
import Models.QuizCategory;

public class AllQuizActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quiz);
        imageView=findViewById(R.id.backpr);
        recyclerView = findViewById(R.id.recyclerViewcategory);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<QuizCategory> categories = new ArrayList<>();
        categories.add(new QuizCategory("Technology", R.drawable.technology));
        categories.add(new QuizCategory("Programming", R.drawable.programming));

        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        recyclerView.setAdapter(adapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}