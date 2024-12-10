package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private ProgressBar progressBar;
    private TextView textView;
    private ArrayList<User> arrayList;
    private ArrayList<String> userids;
    private FollowersAdaptor adaptor;
    private DatabaseReference databaseReference, userref;
    private ImageView back;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recyclerView = findViewById(R.id.recyculerfl);
        arrayList = new ArrayList<>();
        userids = new ArrayList<>();
        textView=findViewById(R.id.nod);
        progressBar=findViewById(R.id.progressBarfol);
        back=findViewById(R.id.backpr);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        userid = getIntent().getStringExtra("userid");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("followers").child(userid);
        fetchfollowers();
        adaptor = new FollowersAdaptor(arrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void fetchfollowers() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot useri : snapshot.getChildren()) {
                        if (useri.getKey() != null) {
                            userids.add(useri.getKey());
                        }
                    }
                    fetchusers();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);

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

                            }
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adaptor.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                Toast.makeText(FollowersActivity.this, "Failed to fetch users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

    