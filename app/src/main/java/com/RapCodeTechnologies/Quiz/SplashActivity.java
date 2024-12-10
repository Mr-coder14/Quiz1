package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();


        new Handler().postDelayed(() -> {
            FirebaseUser userid = auth.getCurrentUser();
            Intent intent;
            if (userid != null) {
                intent = new Intent(SplashActivity.this, UserMainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
