package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AlertDialog;

public class SettingsActivity extends AppCompatActivity {
    private TextView termsConditions,privacyPolicy,contactUs,aboutUs,logout;
    private ImageButton imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize TextViews
       termsConditions = findViewById(R.id.terms_conditions);
        privacyPolicy = findViewById(R.id.privacy_policy);
        imageView=findViewById(R.id.back_btnsearch);
        contactUs = findViewById(R.id.contact_us);
         aboutUs = findViewById(R.id.about_us);
         logout = findViewById(R.id.logout);

         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 finish();
             }
         });

        // Set OnClickListeners
        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/quizfest-termsandconditions/home"));
                startActivity(browserIntent);

            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/quizfest-privacypolicy/home"));
                startActivity(browserIntent);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(SettingsActivity.this,AboutUsActivity.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout operation
                // Add your logout logic here
                showLogoutDialog(); // Close the current activity
            }
        });
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    // Method to perform logout
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }
}