package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.ProfileActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private AppCompatButton globalLeaderBtn;
    private AppCompatButton friendLeaderBtn;
    private ProgressBar progressBar;
    private LinearLayout layout,firstplace,seocndplace,thirdplace;
    private DatabaseReference databaseReference;

    public LeaderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_leader, container, false);
        recyclerView=view.findViewById(R.id.recyculerleaders);
        leaderBoards = new ArrayList<>();
        firstplacename=view.findViewById(R.id.firstpalceusername);
        secondplacename=view.findViewById(R.id.secondpalceusername);
        thirdplacename=view.findViewById(R.id.thirdpalceusername);
        thirdplacecoin=view.findViewById(R.id.thirdrdplacecoin);
        progressBar = view.findViewById(R.id.progressBarleader);
        firstplace=view.findViewById(R.id.firstplace);
        seocndplace=view.findViewById(R.id.secondplace);
        thirdplace=view.findViewById(R.id.thirdplace);
        layout=view.findViewById(R.id.layoutleaders);
        secondplaecoin=view.findViewById(R.id.secondplacecoin);
        firplacecoin=view.findViewById(R.id.firstplacecoin);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        firstpalceimg = view.findViewById(R.id.firstplaceimageview);
        secongplaceimg=view.findViewById(R.id.secondimageview);
        thirdplaceimg=view.findViewById(R.id.thirdplaceimageview);
        globalLeaderBtn = view.findViewById(R.id.Globalleader);
        friendLeaderBtn = view.findViewById(R.id.friendleader);
        userid=FirebaseAuth.getInstance().getUid();
        fetchcoins();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptor=new LeadersAdaptor(leaderBoards);
        recyclerView.setAdapter(adaptor);
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        globalLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
        globalLeaderBtn.setTextColor(getResources().getColor(R.color.white));
        friendLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
        friendLeaderBtn.setTextColor(getResources().getColor(R.color.black));
        firstplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("userid", allUsers.get(0).getUserid());
                startActivity(intent);
            }
        });
        seocndplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allUsers.size() > 1) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userid", allUsers.get(1).getUserid()

                    );
                    startActivity(intent);
                }
            }
        });
        thirdplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allUsers.size() > 2) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userid", allUsers.get(2).getUserid());
                    startActivity(intent);
                }
            }
        });
        globalLeaderBtn.setOnClickListener(v -> {
            globalLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
            globalLeaderBtn.setTextColor(getResources().getColor(R.color.white));
            friendLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
            friendLeaderBtn.setTextColor(getResources().getColor(R.color.black));

        });

        friendLeaderBtn.setOnClickListener(v -> {
            friendLeaderBtn.setBackgroundResource(R.drawable.blue_bg);
            friendLeaderBtn.setTextColor(getResources().getColor(R.color.white));
            globalLeaderBtn.setBackgroundResource(R.drawable.gray_bg);
            globalLeaderBtn.setTextColor(getResources().getColor(R.color.black));

        });
        return view;
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

                        allUsers.add(new LeaderBoard(name, userId, coin, 0));
                    }


                    allUsers.sort((user1, user2) -> Integer.compare(user2.getCoin(), user1.getCoin()));

                    if (allUsers.size() > 0) {
                        LeaderBoard first = allUsers.get(0);
                        firstplacename.setText(first.getUsername());
                        firplacecoin.setText(String.valueOf(first.getCoin()));
                    }

                    if (allUsers.size() > 1) {
                        LeaderBoard second = allUsers.get(1);
                        secondplacename.setText(second.getUsername());
                        secondplaecoin.setText(String.valueOf(second.getCoin()));
                    }

                    if (allUsers.size() > 2) {
                        LeaderBoard third = allUsers.get(2);
                        thirdplacename.setText(third.getUsername());
                        thirdplacecoin.setText(String.valueOf(third.getCoin()));
                    }

                    for (int i = 3; i < allUsers.size(); i++) {
                        LeaderBoard user = allUsers.get(i);
                        user.setNo(i + 1);
                        leaderBoards.add(user);
                    }


                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(() -> {
                            adaptor.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                        });
                    }
                } else {
                    if(getActivity()!=null){
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getActivity()!=null) {
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}