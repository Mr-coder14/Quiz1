package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
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
    private EditText password, repassword, email, name, phno;
    private Button register;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        password = findViewById(R.id.passwordregister);
        repassword = findViewById(R.id.repasswordregister);
        email = findViewById(R.id.emailregister);
        phno = findViewById(R.id.phnoregister);
        progressDialog = new ProgressDialog(this);
        name = findViewById(R.id.nameregister);
        register = findViewById(R.id.registerrbtn);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regiser();
            }
        });
    }

    private void regiser() {


        String emaill = email.getText().toString();
        String repass = repassword.getText().toString();
        String pass = password.getText().toString();
        String na = name.getText().toString();
        String phno1 = phno.getText().toString();

        if (TextUtils.isEmpty(emaill) || TextUtils.isEmpty(repass) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(na)) {
            Toast.makeText(this, "Enter All Details", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password should be minimum 6 characters", Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(repass)) {
            Toast.makeText(RegisterActivity.this, "Enter same passwords", Toast.LENGTH_SHORT).show();
        } else {
            createuser(emaill, pass, na, phno1);
        }

    }

    private void createuser(String emaill, String pass, String name, String phno) {
        auth.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        User newUser = new User(name, emaill, phno,userId,0);
                        databaseReference.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(RegisterActivity.this, "Failed to create an account. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }