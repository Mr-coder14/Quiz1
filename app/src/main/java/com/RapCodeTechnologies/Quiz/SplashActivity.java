package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private boolean isActivityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize ProgressBar


        new Handler().postDelayed(() -> {
            if (!isActivityStarted) {
                FirebaseUser userid = auth.getCurrentUser();
                Intent intent;

                if (userid != null) {
                    intent = new Intent(SplashActivity.this, UserMainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                // Ensure no duplicate activity launches
                isActivityStarted = true;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
