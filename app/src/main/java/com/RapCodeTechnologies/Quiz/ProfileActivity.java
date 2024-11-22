package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView username,coins,rank,followers,bio,txt;
    private DatabaseReference database,requests;
    private String userid_current,userid;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ScrollView lo;
    private LinearLayout message,connect;
    private User user,currentuser;

    private FirebaseUser us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username=findViewById(R.id.usernametxt);
        progressBar = findViewById(R.id.progressBarprofile);
        lo=findViewById(R.id.lo);
        lo.setVisibility(View.GONE);
        connect=findViewById(R.id.connectButton);
        progressBar.setVisibility(View.VISIBLE);
        txt=findViewById(R.id.erex);
        coins=findViewById(R.id.pointsprofile);
        rank=findViewById(R.id.rankprofile);
        followers=findViewById(R.id.Followersprofile);
        auth=FirebaseAuth.getInstance();
        us=auth.getCurrentUser();
        message=findViewById(R.id.messageButton);
        userid_current= FirebaseAuth.getInstance().getUid();
        userid=getIntent().getStringExtra("userid");
        requests=FirebaseDatabase.getInstance().getReference().child("requests").child(userid).child(userid_current);
        userinformation();
        currentuserinfo();

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requests.setValue(currentuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "Requested", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.fees).setVisibility(View.GONE);
                            txt.setText("Requested");
                        }
                    }
                });
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
                intent.putExtra("usermodel",user);
                startActivity(intent);
            }
        });



    }

    private void currentuserinfo() {
        database= FirebaseDatabase.getInstance().getReference().child("users").child(userid_current);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentuser=snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userinformation() {
        database= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    if (user != null) {
                        coins.setText(String.valueOf(user.getCoin()));
                        username.setText(user.getName());
                        rank.setText("#1");
                        followers.setText("100");
                    }
                }
                progressBar.setVisibility(View.GONE);
                lo.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                lo.setVisibility(View.VISIBLE);
            }
        });
    }
}