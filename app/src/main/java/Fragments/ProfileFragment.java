package Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.EditActivity;
import com.RapCodeTechnologies.Quiz.FollowersActivity;
import com.RapCodeTechnologies.Quiz.LoginActivity;
import com.RapCodeTechnologies.Quiz.R;
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

import Adaptors.RecentQuizesAdaptor;
import Models.User;

public class ProfileFragment extends Fragment {
    private TextView username, coins, rank, followers, bio;
    private DatabaseReference database, recent;
    private String userid;
    private FirebaseAuth auth;
    private ArrayList<String> arrayLists;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShapeableImageView img;
    private ScrollView lo;
    private LinearLayout edit, follews;

    private User user;
    private FirebaseUser us;

    private boolean isUserInfoLoaded = false;
    private boolean isFollowersCountLoaded = false;
    private boolean isRecentQuizzesLoaded = false;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_profile, container, false);

        username = view.findViewById(R.id.usernametxt);
        progressBar = view.findViewById(R.id.progressBarprofile);
        lo = view.findViewById(R.id.lo);
        lo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recentquizes);
        arrayLists = new ArrayList<>();
        bio = view.findViewById(R.id.userBio);
        follews = view.findViewById(R.id.follews);
        coins = view.findViewById(R.id.pointsprofile);
        rank = view.findViewById(R.id.rankprofile);
        img = view.findViewById(R.id.profileImage);
        followers = view.findViewById(R.id.Followersprofile);
        auth = FirebaseAuth.getInstance();
        edit = view.findViewById(R.id.editIcon);
        us = auth.getCurrentUser();
        userid = FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        recent = FirebaseDatabase.getInstance().getReference().child("recent_quizzes").child(userid);

        userinformation();
        fetchFollowersCount();
        fetchrecentquizzes();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RecentQuizesAdaptor adaptor = new RecentQuizesAdaptor(arrayLists, getContext());
        recyclerView.setAdapter(adaptor);

        follews.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("userid", userid);
            getActivity().startActivity(intent);
        });

        edit.setOnClickListener(view12 -> {
            Intent intent = new Intent(getContext(), EditActivity.class);
            startActivityForResult(intent, 100);
        });

        return view;
    }

    private void fetchrecentquizzes() {
        recent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey() != null) {
                            arrayLists.add(dataSnapshot.getKey());
                        }
                    }
                }
                isRecentQuizzesLoaded = true;
                checkDataLoadCompletion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isRecentQuizzesLoaded = true;
                checkDataLoadCompletion();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            userinformation(); // Refresh the profile data
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
                    followers.setText(String.valueOf(count));
                } else {
                    followers.setText("0");
                }
                isFollowersCountLoaded = true;
                checkDataLoadCompletion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followers.setText("0");
                isFollowersCountLoaded = true;
                checkDataLoadCompletion();
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
                            if (!isAdded()) {
                                return;
                            }
                            String imageKey = user.getProfile();
                            if (!TextUtils.isEmpty(imageKey)) {
                                int imageResId = getResources().getIdentifier(imageKey, "drawable", requireContext().getPackageName());
                                img.setImageResource(imageResId != 0 ? imageResId : R.drawable.unknownprofile);
                            } else {
                                img.setImageResource(R.drawable.unknownprofile);
                            }
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
                isUserInfoLoaded = true;
                checkDataLoadCompletion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUserInfoLoaded = true;
                checkDataLoadCompletion();
            }
        });
    }

    private void checkDataLoadCompletion() {
        if (isUserInfoLoaded && isFollowersCountLoaded && isRecentQuizzesLoaded) {
            progressBar.setVisibility(View.GONE);
            lo.setVisibility(View.VISIBLE);
        }
    }
}
