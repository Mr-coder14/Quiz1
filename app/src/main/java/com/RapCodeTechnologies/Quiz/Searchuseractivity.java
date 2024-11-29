package com.RapCodeTechnologies.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
    private ArrayList<User> userModels=new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuseractivity);
        txt=findViewById(R.id.search_edittxt);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        back=findViewById(R.id.back_btnsearch);
        progressBar=findViewById(R.id.progresssearch);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (!dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) {
                            User userModel = dataSnapshot.getValue(User.class);
                            userModels.add(userModel);
                        }
                    }

                    searchadaptor.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Searchuseractivity.this, "NO Users Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Searchuseractivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView=findViewById(R.id.search_recycular_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);
        searchadaptor=new searchadaptor(this,userModels);
        recyclerView.setAdapter(searchadaptor);
    }
}