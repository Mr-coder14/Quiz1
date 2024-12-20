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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adaptors.RecentQuizesAdaptor;
import Models.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView username,coins,rank,followers,bio,txt,higheststrikerate,currentstrike;
    private DatabaseReference database,requests;
    private String userid_current,userid;
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private ArrayList<String > arrayLists;
    private ProgressBar progressBar;
    private boolean isDataLoaded = false;
    private NestedScrollView lo;
    private LinearLayout message,connect,g;
    private ImageView imageView,backpr,more;
    private LinearLayout layout;
    private User user,currentuser;
    private ShapeableImageView img;

    private DatabaseReference followersRef,recent;
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
        recyclerView=findViewById(R.id.recentquizes);
        connect=findViewById(R.id.connectButton);
        arrayLists=new ArrayList<>();
        g=findViewById(R.id.fe);
        more=findViewById(R.id.more);
        higheststrikerate=findViewById(R.id.higheststrikerate);
        currentstrike=findViewById(R.id.currentstrike);
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
        recent=FirebaseDatabase.getInstance().getReference().child("recent_quizzes").child(userid);
        findViewById(R.id.fees).setVisibility(View.VISIBLE);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_person_add_alt_1_24));
//        checkIfBlocked();
//        userinformation();

        fetchDataAndInitialize();
        RecentQuizesAdaptor adaptor=new RecentQuizesAdaptor(arrayLists,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adaptor);
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
                } else if (currentStatus.equals("UnBlock")) {
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("UnBlock User")
                            .setMessage("Are you sure you want to UnBlock this user?")
                            .setPositiveButton("Yes", (dialog, which) -> handleUnblockUser())
                            .setNegativeButton("No", null)
                            .show();
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
    private void finalizeUI() {
        progressBar.setVisibility(View.GONE);
        lo.setVisibility(View.VISIBLE);

        // Update RecyclerView adapter
        RecentQuizesAdaptor adaptor = new RecentQuizesAdaptor(arrayLists, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adaptor);
    }

    private void fetchrecentquizzes() {
        recent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(dataSnapshot.getKey()!=null){
                            arrayLists.add(dataSnapshot.getKey());
                        }
                    }
                }
                else {
                    recyclerView.setVisibility(View.GONE);
                    g.setVisibility(View.VISIBLE);
                }
                fetchFollowersCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                fetchFollowersCount();
                recyclerView.setVisibility(View.GONE);
                g.setVisibility(View.VISIBLE);

            }
        });
    }

    private void checkIfBlocked() {
        DatabaseReference blockedUsersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("blocked_users")
                .child(userid)
                .child(userid_current);

        blockedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    lo.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "You cannot view this profile", Toast.LENGTH_SHORT).show();
                } else {

                    checkRequestStatus();
                    updateImageViewBasedOnText();
                    userinformation();
                    currentuserinfo();
                    checkisfollowed();
                    fetchFollowersCount();
                    lo.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lo.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Failed to check block status", Toast.LENGTH_SHORT).show();
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
        checkRequestStatus();
        updateImageViewBasedOnText();
    }

    private void handleReportUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Report User");


        View reportView = getLayoutInflater().inflate(R.layout.dialog_report_user, null);
        RadioGroup reportReasons = reportView.findViewById(R.id.radioGroupReportReasons);

        builder.setView(reportView);

        builder.setPositiveButton("Submit Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int selectedReasonId = reportReasons.getCheckedRadioButtonId();

                if (selectedReasonId == -1) {
                    // No reason selected
                    Toast.makeText(ProfileActivity.this, "Please select a reason for reporting", Toast.LENGTH_SHORT).show();
                    return;
                }


                RadioButton selectedReason = reportView.findViewById(selectedReasonId);
                String reportReason = selectedReason.getText().toString();


                submitReport(reportReason);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void submitReport(String reportReason) {
        // Get Firebase Database reference for reported users
        DatabaseReference reportedUsersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("reported_users")
                .child(userid); // The ID of the user being reported

        // Create a unique key for each report
        String reportId = reportedUsersRef.push().getKey();

        // Create a report object
        if (reportId != null) {
            // Create a map to store report details
            Map<String, Object> reportDetails = new HashMap<>();
            reportDetails.put("reported_by", userid_current); // ID of the user making the report
            reportDetails.put("reason", reportReason);
            reportDetails.put("timestamp", System.currentTimeMillis());

            // Submit the report
            reportedUsersRef.child(reportId).setValue(reportDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "User reported successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private void updateImageViewBasedOnText() {
        String text = txt.getText().toString();
        if (text.equals("Connect")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_person_add_alt_1_24));
        } else if (text.equals("Followed")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_24));
        }
        else if(text.equals("UnBlock")){
            imageView.setVisibility(View.GONE);
        }
        else if (text.equals("Requested")) {
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
                finalizeUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followers.setText("0");
                finalizeUI();
            }
        });
    }


    private void checkRequestStatus() {
        DatabaseReference combinedStatusRef = FirebaseDatabase.getInstance().getReference();

        combinedStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean isBlocked = snapshot.child("blocked_users")
                        .child(userid_current)
                        .child(userid)
                        .exists();

                if (isBlocked) {
                    txt.setText("UnBlock");
                    message.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    return;
                }

                // If not blocked, check other statuses
                boolean isFollower = snapshot.child("followers")
                        .child(userid)
                        .child(userid_current)
                        .exists();

                boolean isRequestSent = snapshot.child("requests")
                        .child(userid)
                        .child(userid_current)
                        .exists();

                if (isFollower) {
                    txt.setText("Followed");
                    message.setVisibility(View.VISIBLE);
                } else if (isRequestSent) {
                    txt.setText("Requested");
                    message.setVisibility(View.GONE);
                } else {
                    txt.setText("Connect");
                    message.setVisibility(View.GONE);
                }

                updateImageViewBasedOnText();
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
                            higheststrikerate.setText(String.valueOf(user.getHigheststrike()));
                            currentstrike.setText(String.valueOf(user.getCurrentstrike()));
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


                    fetchrecentquizzes();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchDataAndInitialize() {
        checkRequestStatus();
        updateImageViewBasedOnText();
        userinformation();
        currentuserinfo();
        checkisfollowed();
        fetchFollowersCount();


        progressBar.setVisibility(View.VISIBLE);
        lo.setVisibility(View.GONE);


        isDataLoaded = true;
        updateVisibility();
    }

    private void updateVisibility() {
        if (isDataLoaded) {
            progressBar.setVisibility(View.GONE);
            lo.setVisibility(View.VISIBLE);
        }
    }
    private void handleUnblockUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("blocked_users")
                .child(userid_current)
                .child(userid);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "User unblocked successfully", Toast.LENGTH_SHORT).show();
                fetchDataAndInitialize();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to unblock user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}