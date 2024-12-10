package com.RapCodeTechnologies.Quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    private ImageView imageView,backpr,more;
    private LinearLayout layout;
    private User user,currentuser;
    private ShapeableImageView img;
    private boolean isRequestSent = false;
    private DatabaseReference followersRef;
    private FirebaseUser us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        layout=findViewById(R.id.folowes);
        username=findViewById(R.id.usernametxt);
        progressBar = findViewById(R.id.progressBarprofile);
        lo=findViewById(R.id.lo);
        lo.setVisibility(View.GONE);
        connect=findViewById(R.id.connectButton);
        more=findViewById(R.id.more);
        img=findViewById(R.id.profileImage);
        backpr=findViewById(R.id.backpr);
        imageView=findViewById(R.id.fees);
        progressBar.setVisibility(View.VISIBLE);
        txt=findViewById(R.id.erex);
        coins=findViewById(R.id.pointsprofile);
        rank=findViewById(R.id.rankprofile);
        followers=findViewById(R.id.Followersprofile);
        bio=findViewById(R.id.userBio);
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
        checkisfollowed();
        fetchFollowersCount();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,FollowersActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });
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
                            .setNegativeButton("No", null)
                            .show();
                } else if (currentStatus.equals("Followed")) {

                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Unfollow")
                            .setMessage("Are you sure you want to unfollow?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

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
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
                intent.putExtra("usermodel",user);
                intent.putExtra("userid",user.getUserid());
                startActivity(intent);
            }
        });
    }

    private void checkisfollowed() {
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                     message.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
                int id=item.getItemId();
               if(id==R.id.action_block){
                   showBlockConfirmationDialog();
                   return true;
                }


                if(id==R.id.action_report){
                handleReportUser();
                return true;
            }


                if(id== R.id.action_share) {
                    shareProfile();
                    return true;
                }
            return false;
        });

        popupMenu.show();
    }
    private void showBlockConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Block User")
                .setMessage("Are you sure you want to block this user?")
                .setPositiveButton("Yes", (dialog, which) -> blockUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void blockUser() {
        DatabaseReference blockListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("blocked_users")
                .child(userid_current)
                .child(userid);

        blockListRef.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "User blocked successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to block user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleReportUser() {

    }

    private void shareProfile() {
        String shareMessage = "Check out this profile on QuizApp: " + user.getName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share Profile Using"));
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
    private void fetchFollowersCount() {
        DatabaseReference followersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("followers")
                .child(userid);

        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount();
                    followers.setText(String.valueOf(count) );
                } else {
                    followers.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followers.setText("0");
            }
        });
    }


    private void checkRequestStatus() {

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


                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User fetchedUser = userSnapshot.getValue(User.class);
                        if (fetchedUser != null) {
                            userList.add(fetchedUser);
                        }
                    }


                    userList.sort((u1, u2) -> Integer.compare(u2.getCoin(), u1.getCoin()));

                    int currentRank = 1;
                    boolean viewedUserRankFound = false;

                    for (User u : userList) {
                        if (u.getUserid().equals(userid)) {
                            user = u;
                            username.setText(user.getName());
                            coins.setText(String.valueOf(user.getCoin()));
                            rank.setText("#" + currentRank);
                            bio.setText(user.getBio()==null || user.getBio()==""? "N/A":user.getBio());
                            String imageKey=user.getProfile();
                            if (!TextUtils.isEmpty(imageKey)) {
                                int imageResId = getResources().getIdentifier(imageKey, "drawable", getPackageName());
                                if (imageResId != 0) {
                                    img.setImageResource(imageResId);
                                } else {
                                    img.setImageResource(R.drawable.unknownprofile);
                                }
                            } else {
                                imageView.setImageResource(R.drawable.unknownprofile);
                            }
                            viewedUserRankFound = true;
                            break;
                        }
                        currentRank++;
                    }

                    if (!viewedUserRankFound) {
                        Toast.makeText(ProfileActivity.this, "User rank not found", Toast.LENGTH_SHORT).show();
                    }


                    progressBar.setVisibility(View.GONE);
                    lo.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    lo.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                }
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