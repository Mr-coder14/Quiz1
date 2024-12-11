package com.RapCodeTechnologies.Quiz;

import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText password, repassword, email, name;
    private Button register;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private CheckBox checkboxTerms, checkboxAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        password = findViewById(R.id.passwordregister);
        repassword = findViewById(R.id.repasswordregister);
        checkboxTerms=findViewById(R.id.checkbox_terms);
        checkboxAge=findViewById(R.id.checkbox_age);
        email = findViewById(R.id.emailregister);
        progressDialog = new ProgressDialog(this);
        name = findViewById(R.id.nameregister);
        register = findViewById(R.id.registerrbtn);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        String fullText = "I accept the Terms and Conditions & Privacy Policy. The Privacy Policy explains how we handle your data securely and ensure your information's confidentiality.";
        SpannableString spannable = new SpannableString(fullText);


        int termsStart = fullText.indexOf("Terms and Conditions");
        int termsEnd = termsStart + "Terms and Conditions".length();
        int policyStart = fullText.indexOf("Privacy Policy");
        int policyEnd = policyStart + "Privacy Policy".length();


        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/quizfest-termsandconditions/home"));
                startActivity(browserIntent);

            }
        };
        spannable.setSpan(termsClick, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.PrimaryColor)), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        ClickableSpan policyClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Handle Privacy Policy link click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/quizfest-privacypolicy/home"));
                startActivity(browserIntent);
            }
        };
        spannable.setSpan(policyClick, policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.PrimaryColor)), policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkboxTerms.setText(spannable);
        checkboxTerms.setMovementMethod(LinkMovementMethod.getInstance());
        register.setOnClickListener(v -> {
            if (!checkboxTerms.isChecked()  || !checkboxAge.isChecked()) {
                StringBuilder message = new StringBuilder("Please agree to:");
                if (!checkboxTerms.isChecked()) message.append("\n- Terms and Conditions & Privacy Policy");
                if (!checkboxAge.isChecked()) message.append("\n- Age requirement");
                Toast.makeText(RegisterActivity.this, message.toString(), Toast.LENGTH_LONG).show();
            } else {
                regiser();
            }
        });

    }

    private void regiser() {


        String emaill = email.getText().toString();
        String repass = repassword.getText().toString();
        String pass = password.getText().toString();
        String na = name.getText().toString();

        if (TextUtils.isEmpty(emaill) || TextUtils.isEmpty(repass) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(na)) {
            Toast.makeText(this, "Enter All Details", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password should be minimum 6 characters", Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(repass)) {
            Toast.makeText(RegisterActivity.this, "Enter same passwords", Toast.LENGTH_SHORT).show();
        } else {
            createuser(emaill, pass, na);
        }

    }

    private void createuser(String emaill, String pass, String name) {

        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        User newUser = new User(name, emaill, userId, 0,"");
                        databaseReference.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, UserMainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed to create an account. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}