package com.RapCodeTechnologies.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adaptors.searchadaptor;
import Models.User;

public class Searchuseractivity extends AppCompatActivity {
    private EditText txt;
    private ImageButton back;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Adaptors.searchadaptor searchadaptor;
    private ArrayList<User> userModels = new ArrayList<>();
    private ArrayList<User> filteredUsers = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuseractivity);

        txt = findViewById(R.id.search_edittxt);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        back = findViewById(R.id.back_btnsearch);
        progressBar = findViewById(R.id.progresssearch);
        recyclerView = findViewById(R.id.search_recycular_view);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load all users from the database
        loadUsers();

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchadaptor = new searchadaptor(this, filteredUsers);
        recyclerView.setAdapter(searchadaptor);

        // Add TextWatcher for search functionality
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Method to load all users from Firebase
    private void loadUsers() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    userModels.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (!dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) {
                            User userModel = dataSnapshot.getValue(User.class);
                            userModels.add(userModel);
                        }
                    }
                    // Initially, show all users
                    filteredUsers.clear();
                    filteredUsers.addAll(userModels);
                    searchadaptor.notifyDataSetChanged();
                } else {
                    Toast.makeText(Searchuseractivity.this, "No Users Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Searchuseractivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter users by username
    private void filterUsers(String query) {
        filteredUsers.clear();

        if (query.isEmpty()) {
            filteredUsers.addAll(userModels); // Show all users when query is empty
        } else {
            for (User user : userModels) {
                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
        }
        searchadaptor.notifyDataSetChanged();
    }
}
