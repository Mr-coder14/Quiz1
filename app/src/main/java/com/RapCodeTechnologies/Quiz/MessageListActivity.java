package com.RapCodeTechnologies.Quiz;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private ImageView backpr;

    private String userid;
    private ArrayList<User> userModels= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        imageButton=findViewById(R.id.searchuser);

        recyclerView=findViewById(R.id.recyluerviewchats);
        progressBar=findViewById(R.id.progressbarchat);

        backpr=findViewById(R.id.backpr);
        progressBar.setVisibility(View.VISIBLE);
        userid= FirebaseAuth.getInstance().getUid();
        db1=FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("chatss").child(userid);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userdetials();
        adaptor = new ChatlistAdaptor(this, userModels,userid);
        recyclerView.setAdapter(adaptor);

        backpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageListActivity.this,Searchuseractivity.class));
            }
        });
    }
    private void userdetials() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userModels.clear();
                    int totalUsers = (int) snapshot.getChildrenCount(); // Total users to load
                    int[] loadedUsers = {0}; // Counter for loaded users

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User userModel = dataSnapshot.getValue(User.class);
                        if (userModel != null) {
                            // Fetch additional details for each user
                            DatabaseReference userDetailsRef = FirebaseDatabase.getInstance()
                                    .getReference().child("users").child(userModel.getUserid());

                            userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        User userDetails = userSnapshot.getValue(User.class);
                                        if (userDetails != null) {
                                            userModel.setName(userDetails.getName());
                                            userModel.setProfile(userDetails.getProfile());
                                        }
                                    }

                                    userModels.add(userModel);
                                    loadedUsers[0]++;

                                    // Check if all users are loaded
                                    if (loadedUsers[0] == totalUsers) {
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adaptor.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    loadedUsers[0]++;
                                    if (loadedUsers[0] == totalUsers) {
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adaptor.notifyDataSetChanged();
                                    }
                                    Toast.makeText(MessageListActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            loadedUsers[0]++;
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(MessageListActivity.this, "No chats found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(MessageListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}