package com.RapCodeTechnologies.Quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Models.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView username,coins,rank,followers,bio,txt;
    private DatabaseReference database,requests;
    private String userid_current,userid;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ScrollView lo;
    private LinearLayout message,connect;
    private ImageView imageView,backpr;
    private User user,currentuser;
    private boolean isRequestSent = false;
    private DatabaseReference followersRef;


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
        backpr=findViewById(R.id.backpr);
        imageView=findViewById(R.id.fees);
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
        followersRef = FirebaseDatabase.getInstance().getReference().child("followers").child(userid).child(userid_current);
        findViewById(R.id.fees).setVisibility(View.VISIBLE);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_person_add_alt_1_24));
        checkRequestStatus();
        updateImageViewBasedOnText();
        userinformation();
        currentuserinfo();
        backpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStatus = txt.getText().toString();

                if (currentStatus.equals("Requested")) {

                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Remove Request")
                            .setMessage("Are you sure you want to remove this request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remove the request from Firebase
                                    requests.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Request Removed", Toast.LENGTH_SHORT).show();
                                                txt.setText("Connect");
                                                updateImageViewBasedOnText();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Failed to remove request", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null) // Do nothing on "No"
                            .show();
                } else if (currentStatus.equals("Followed")) {

                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Unfollow")
                            .setMessage("Are you sure you want to unfollow?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remove the follower
                                    followersRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                                                txt.setText("Connect");
                                                updateImageViewBasedOnText();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Failed to unfollow", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else if (currentStatus.equals("Connect")) {
                    // Send a follow request
                    requests.setValue(currentuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Requested", Toast.LENGTH_SHORT).show();
                                txt.setText("Requested");
                                updateImageViewBasedOnText();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
    private void updateImageViewBasedOnText() {
        String text = txt.getText().toString();
        if (text.equals("Connect")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_person_add_alt_1_24));
        } else if (text.equals("Followed")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_24));
        } else if (text.equals("Requested")) {
            imageView.setVisibility(View.GONE);
        }
    }


    private void checkRequestStatus() {
        // Track the current state to prevent conflicting updates
        DatabaseReference combinedStatusRef = FirebaseDatabase.getInstance().getReference();

        combinedStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFollower = snapshot.child("followers").child(userid).child(userid_current).exists();
                boolean isRequestSent = snapshot.child("requests").child(userid).child(userid_current).exists();

                if (isFollower) {
                    txt.setText("Followed");
                    updateImageViewBasedOnText();
                } else if (isRequestSent) {
                    txt.setText("Requested");
                    updateImageViewBasedOnText();
                } else {
                    txt.setText("Connect");
                    updateImageViewBasedOnText();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error checking status", Toast.LENGTH_SHORT).show();
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
        database = FirebaseDatabase.getInstance().getReference().child("users");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<User> userList = new ArrayList<>();

                    // Collect all users
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User fetchedUser = userSnapshot.getValue(User.class);
                        if (fetchedUser != null) {
                            userList.add(fetchedUser);
                        }
                    }

                    // Sort users by coin in descending order
                    Collections.sort(userList, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            return Integer.compare(u2.getCoin(), u1.getCoin());
                        }
                    });

                    int currentRank = 1;
                    boolean viewedUserRankFound = false;
                    boolean currentUserRankFound = false;

                    // Assign ranks based on sorted list
                    for (User u : userList) {
                        // Check for the user being viewed
                        if (!viewedUserRankFound && u.getUserid().equals(userid)) {
                            user = u;
                            coins.setText(String.valueOf(user.getCoin()));
                            username.setText(user.getName());
                            followers.setText("100"); // Static value
                            rank.setText("#" + currentRank); // Set rank for the viewed user
                            viewedUserRankFound = true;
                        }

                        // Check for the current logged-in user's rank
                        if (!currentUserRankFound && u.getUserid().equals(userid_current)) {
                            rank.setText("#" + currentRank); // Set rank for the current user
                            currentUserRankFound = true;
                        }

                        currentRank++;
                    }

                    // If the viewed user is not found, show a message
                    if (!viewedUserRankFound) {
                        Toast.makeText(ProfileActivity.this, "Viewed user not found", Toast.LENGTH_SHORT).show();
                    }
                }

                progressBar.setVisibility(View.GONE);
                lo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                lo.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }




}