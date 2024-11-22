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

import com.RapCodeTechnologies.Quiz.LoginActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.User;

public class ProfileFragment extends Fragment {
    private TextView username,coins,rank,followers;
    private DatabaseReference database;
    private String userid;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ScrollView lo;


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
        progressBar.setVisibility(View.VISIBLE);
        coins=view.findViewById(R.id.pointsprofile);
        rank=view.findViewById(R.id.rankprofile);
        followers=view.findViewById(R.id.Followersprofile);
        auth=FirebaseAuth.getInstance();
        us=auth.getCurrentUser();
        userid= FirebaseAuth.getInstance().getUid();
        database=FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        userinformation();
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
    private void userinformation() {
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