package Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.EditActivity;
import com.RapCodeTechnologies.Quiz.LoginActivity;
import com.RapCodeTechnologies.Quiz.R;
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

public class ProfileFragment extends Fragment {
    private TextView username,coins,rank,followers,bio;
    private DatabaseReference database;
    private String userid;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ScrollView lo;
    private LinearLayout edit;


    private User user;
    private FirebaseUser us;

    public ProfileFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_profile,container,false);

        username=view.findViewById(R.id.usernametxt);
        progressBar = view.findViewById(R.id.progressBarprofile);
        lo=view.findViewById(R.id.lo);
        lo.setVisibility(View.GONE);
        bio=view.findViewById(R.id.userBio);
        progressBar.setVisibility(View.VISIBLE);
        coins=view.findViewById(R.id.pointsprofile);
        rank=view.findViewById(R.id.rankprofile);
        followers=view.findViewById(R.id.Followersprofile);
        auth=FirebaseAuth.getInstance();
        edit=view.findViewById(R.id.editIcon);
        us=auth.getCurrentUser();
        userid= FirebaseAuth.getInstance().getUid();
        database=FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        userinformation();
        fetchFollowersCount();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditActivity.class));
            }
        });
        return view;
    }
    private void dailogbox() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to Logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (us != null) {
                    auth.signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
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

    private void userinformation() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    boolean userRankFound = false;


                    for (User u : userList) {
                        if (u.getUserid().equals(userid)) {
                            user = u;
                            coins.setText(String.valueOf(user.getCoin()));
                            username.setText(user.getName());
                            bio.setText(user.getBio());
                            rank.setText("#" + currentRank);
                            userRankFound = true;
                            break;
                        }
                        currentRank++;
                    }


                    if (!userRankFound) {
                        rank.setText("N/A");
                        coins.setText("0");
                        username.setText("Unknown");
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