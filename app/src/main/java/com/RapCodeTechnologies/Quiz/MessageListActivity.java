package com.RapCodeTechnologies.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import Adaptors.ChatlistAdaptor;
import Models.User;


public class MessageListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageButton imageButton;
    private ChatlistAdaptor adaptor;
    private DatabaseReference databaseReference,db1;
    private ProgressBar progressBar;
    private String userid;
    private ArrayList<User> userModels= new ArrayList<>();
    private ArrayList<User> users= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        imageButton=findViewById(R.id.searchuser);
        recyclerView=findViewById(R.id.recyluerviewchats);
        progressBar=findViewById(R.id.progressbarchat);
        progressBar.setVisibility(View.VISIBLE);

        db1=FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setVisibility(View.GONE);
        userid= FirebaseAuth.getInstance().getUid();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new ChatlistAdaptor(this, userModels,userid);
        recyclerView.setAdapter(adaptor);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("chatss").child(userid);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageListActivity.this,Searchuseractivity.class));

            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userModels.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User userModel=dataSnapshot.getValue(
                                User.class);
                        userModels.add(userModel);
                    }
                    adaptor.notifyDataSetChanged();
                }else {
                    Toast.makeText(MessageListActivity.this, "No chats found", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

    }
}