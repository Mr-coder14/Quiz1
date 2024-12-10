package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adaptors.FollowersAdaptor;
import Models.User;

public class FollowersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> arrayList;
    private ArrayList<String> userids;
    private FollowersAdaptor adaptor;
    private DatabaseReference databaseReference, userref;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recyclerView = findViewById(R.id.recyculerfl);
        arrayList = new ArrayList<>();
        userids = new ArrayList<>();
        userid = getIntent().getStringExtra("userid");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("followers").child(userid);
        fetchfollowers();
        adaptor = new FollowersAdaptor(arrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);


    }

    private void fetchfollowers() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot useri : snapshot.getChildren()) {
                        if (useri.getKey() != null) {
                            userids.add(useri.getKey());
                            Toast.makeText(FollowersActivity.this, useri.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    fetchusers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchusers() {
        userref = FirebaseDatabase.getInstance().getReference().child("users");
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String key = userSnapshot.getKey();
                        if (key != null && userids.contains(key)) {

                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                arrayList.add(user);
                                Toast.makeText(FollowersActivity.this, user.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    adaptor.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FollowersActivity.this, "Failed to fetch users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

    