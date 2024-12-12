package Fragments;

import static com.google.common.reflect.Reflection.getPackageName;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.RapCodeTechnologies.Quiz.ProfileActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adaptors.LeadersAdaptor;
import Models.LeaderBoard;


public class LeaderFragment extends Fragment {
    private ArrayList<LeaderBoard> leaderBoards;
    private ArrayList<LeaderBoard> allUsers;
    private RecyclerView recyclerView;
    private LeadersAdaptor adaptor;
    private TextView firstplacename,secondplacename,thirdplacename,firplacecoin,secondplaecoin,thirdplacecoin;
    private ImageView firstpalceimg,secongplaceimg,thirdplaceimg;
    private String userid;
    private ArrayList<String> userids;
    private AppCompatButton globalLeaderBtn;
    private AppCompatButton friendLeaderBtn;
    private LinearLayout notfound,loo;
    private ProgressBar progressBar;
    private LinearLayout layout,firstplace,seocndplace,thirdplace;
    private DatabaseReference databaseReference,followersref;

    public LeaderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_leader, container, false);
        recyclerView=view.findViewById(R.id.recyculerleaders);
        leaderBoards = new ArrayList<>();
        userids=new ArrayList<>();
        firstplacename=view.findViewById(R.id.firstpalceusername);
        notfound=view.findViewById(R.id.notfound);
        secondplacename=view.findViewById(R.id.secondpalceusername);
        thirdplacename=view.findViewById(R.id.thirdpalceusername);
        thirdplacecoin=view.findViewById(R.id.thirdrdplacecoin);
        progressBar = view.findViewById(R.id.progressBarleader);
        firstplace=view.findViewById(R.id.firstplace);
        followersref=FirebaseDatabase.getInstance().getReference().child("followers");
        seocndplace=view.findViewById(R.id.secondplace);
        thirdplace=view.findViewById(R.id.thirdplace);
        layout=view.findViewById(R.id.layoutleaders);
        loo=view.findViewById(R.id.headerd);
        notfound.setVisibility(View.GONE);
        secondplaecoin=view.findViewById(R.id.secondplacecoin);
        firplacecoin=view.findViewById(R.id.firstplacecoin);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        firstpalceimg = view.findViewById(R.id.firstplaceimageview);
        secongplaceimg=view.findViewById(R.id.secondimageview);
        thirdplaceimg=view.findViewById(R.id.thirdplaceimageview);
        globalLeaderBtn = view.findViewById(R.id.Globalleader);
        friendLeaderBtn = view.findViewById(R.id.friendleader);
        userid=FirebaseAuth.getInstance().getUid();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptor=new LeadersAdaptor(leaderBoards);
        recyclerView.setAdapter(adaptor);
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        fetchcoins();
        globalLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
        globalLeaderBtn.setTextColor(getResources().getColor(R.color.white));
        friendLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
        friendLeaderBtn.setTextColor(getResources().getColor(R.color.black));
        firstplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allUsers.get(0).getUserid().equals(userid)){
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userid", allUsers.get(0).getUserid());
                    startActivity(intent);
                }

            }
        });
        seocndplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allUsers.size() > 1) {

                    if(!allUsers.get(1).getUserid().equals(userid)){
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("userid", allUsers.get(1).getUserid());
                        startActivity(intent);
                    }
                }
            }
        });
        thirdplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allUsers.size() > 2) {
                    if(!allUsers.get(2).getUserid().equals(userid)){
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("userid", allUsers.get(2).getUserid());
                        startActivity(intent);
                    }
                }
            }
        });
        globalLeaderBtn.setOnClickListener(v -> {
            globalLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
            globalLeaderBtn.setTextColor(getResources().getColor(R.color.white));
            friendLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
            friendLeaderBtn.setTextColor(getResources().getColor(R.color.black));
            notfound.setVisibility(View.GONE);
            fetchcoins();
        });

        friendLeaderBtn.setOnClickListener(v -> {
            friendLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
            friendLeaderBtn.setTextColor(getResources().getColor(R.color.white));
            globalLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
            globalLeaderBtn.setTextColor(getResources().getColor(R.color.black));
            fetchFriendsCoins();

        });
        return view;
    }
    private void fetchFriendsCoins() {
        progressBar.setVisibility(View.VISIBLE);

        followersref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userids.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot followedUser : snapshot.getChildren()) {
                        if (followedUser.hasChild(userid)) {
                            String followedUserId = followedUser.getKey();
                            if (followedUserId != null) {
                                userids.add(followedUserId);
                            }
                        }
                    }

                    if (userids.isEmpty()) {
                        // No followers found
                        handleNoFollowers();
                    } else {
                        // Fetch details of followers
                        fetchFriendsData();
                    }
                } else {
                    handleNoFollowers(); // If snapshot does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Failed to fetch followers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNoFollowers() {
        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.GONE); // Hide leaderboard layout
        recyclerView.setVisibility(View.GONE); // Hide RecyclerView
        firstplace.setVisibility(View.GONE); // Hide first place ranking
        seocndplace.setVisibility(View.GONE); // Hide second place ranking
        thirdplace.setVisibility(View.GONE); // Hide third place ranking
        notfound.setVisibility(View.VISIBLE);
        loo.setVisibility(View.VISIBLE);// Show "Not Found" layout
    }



    private void fetchFriendsData() {
        leaderBoards.clear();
        allUsers = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!userids.isEmpty() && snapshot.exists()) {
                    for (String friendId : userids) {
                        DataSnapshot userSnapshot = snapshot.child(friendId);
                        String name = userSnapshot.child("name").getValue(String.class);
                        String userId = userSnapshot.child("userid").getValue(String.class);
                        int coin = userSnapshot.child("coin").getValue(Integer.class);
                        String profile=userSnapshot.child("profile").getValue(String.class);

                        allUsers.add(new LeaderBoard(name, userId, coin, 0,profile));
                    }

                    // Add current user to friends list
                    DataSnapshot currentUserSnapshot = snapshot.child(userid);
                    String name = currentUserSnapshot.child("name").getValue(String.class);
                    int coin = currentUserSnapshot.child("coin").getValue(Integer.class);
                    String profile=currentUserSnapshot.child("profile").getValue(String.class);
                    allUsers.add(new LeaderBoard(name, userid, coin, 0,profile));

                    // Sort friends by coins
                    allUsers.sort((user1, user2) -> Integer.compare(user2.getCoin(), user1.getCoin()));

                    // Update leaderboard UI
                    updateTopPlaces();

                    // Add remaining users to leaderboard
                    for (int i = 3; i < allUsers.size(); i++) {
                        LeaderBoard user = allUsers.get(i);
                        user.setNo(i + 1);
                        leaderBoards.add(user);
                    }

                    // Update UI
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            adaptor.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.VISIBLE);
                            notfound.setVisibility(View.GONE);
                            loo.setVisibility(View.VISIBLE);// Hide "Not Found"
                        });
                    }
                } else {
                    handleNoFollowers(); // No data found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleNoFollowers(); // Handle fetch error gracefully
            }
        });
    }


    private void updateTopPlaces() {
        if (allUsers.size() > 0) {
            LeaderBoard first = allUsers.get(0);
            firstplacename.setText(first.getUsername());
            firplacecoin.setText(String.valueOf(first.getCoin()));
            String imageKey = first.getProfile();
            if (!TextUtils.isEmpty(imageKey)) {
                int imageResId = getResources().getIdentifier(imageKey, "drawable", getContext().getPackageName());
                if (imageResId != 0) {
                    firstpalceimg.setImageResource(imageResId);
                } else {
                    firstpalceimg.setImageResource(R.drawable.unknownprofile);
                }
            } else {
                firstpalceimg.setImageResource(R.drawable.unknownprofile);
            }
        } else {
            firstplace.setVisibility(View.GONE);
        }

        if (allUsers.size() > 1) {
            LeaderBoard second = allUsers.get(1);
            secondplacename.setText(second.getUsername());
            secondplaecoin.setText(String.valueOf(second.getCoin()));
            String imageKey = second.getProfile();
            if (!TextUtils.isEmpty(imageKey)) {
                int imageResId = getResources().getIdentifier(imageKey, "drawable", getContext().getPackageName());
                if (imageResId != 0) {
                    secongplaceimg.setImageResource(imageResId);
                } else {
                    secongplaceimg.setImageResource(R.drawable.unknownprofile);
                }
            } else {
                secongplaceimg.setImageResource(R.drawable.unknownprofile);
            }
            seocndplace.setVisibility(View.VISIBLE);
        } else {
            seocndplace.setVisibility(View.GONE);
        }

        if (allUsers.size() > 2) {
            LeaderBoard third = allUsers.get(2);
            thirdplacename.setText(third.getUsername());
            thirdplacecoin.setText(String.valueOf(third.getCoin()));
            String imageKey = third.getProfile();
            if (!TextUtils.isEmpty(imageKey)) {
                int imageResId = getResources().getIdentifier(imageKey, "drawable", getContext().getPackageName());
                if (imageResId != 0) {
                    thirdplaceimg.setImageResource(imageResId);
                } else {
                    thirdplaceimg.setImageResource(R.drawable.unknownprofile);
                }
            } else {
                thirdplaceimg.setImageResource(R.drawable.unknownprofile);
            }
            thirdplace.setVisibility(View.VISIBLE);
        } else {
            thirdplace.setVisibility(View.GONE);
        }
    }


    private void fetchcoins() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    leaderBoards.clear();
                    allUsers = new ArrayList<>();
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String userId = userSnapshot.child("userid").getValue(String.class);
                        int coin = userSnapshot.child("coin").getValue(Integer.class);
                        String p=userSnapshot.child("profile").getValue(String.class);

                        allUsers.add(new LeaderBoard(name, userId, coin, 0,p));
                    }

                    // Sort by coins and ensure no duplicates
                    allUsers.sort((user1, user2) -> Integer.compare(user2.getCoin(), user1.getCoin()));
                    updateTopPlaces();

                    // Add remaining users to leaderboard
                    for (int i = 3; i < allUsers.size(); i++) {
                        LeaderBoard user = allUsers.get(i);
                        user.setNo(i + 1);
                        leaderBoards.add(user);
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            adaptor.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(leaderBoards.isEmpty() ? View.GONE : View.VISIBLE);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}