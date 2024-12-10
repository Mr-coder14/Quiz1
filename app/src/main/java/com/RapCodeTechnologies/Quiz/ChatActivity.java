package com.RapCodeTechnologies.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adaptors.ChatAdapter;
import Models.MessageModel;
import Models.User;


public class ChatActivity extends AppCompatActivity {
    private ImageButton back,send;
    private EditText txt;
    private ProgressBar progressBar;
    private RelativeLayout chat_toolbar,bottom_layout;
    private User userModel;
    private ImageView imageView;
    private LinearLayout profile;
    private TextView username;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModel> messageList;
    private DatabaseReference reference,chatrefrence,useref;
    private String senderRoom, receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        txt=findViewById(R.id.chat_edittxt);
        send=findViewById(R.id.send_message_btn);
        progressBar=findViewById(R.id.progressBarchats);
        progressBar.setVisibility(View.VISIBLE);
        bottom_layout=findViewById(R.id.bottom_layout);
        chat_toolbar=findViewById(R.id.chat_toolbar);
        back=findViewById(R.id.chat_back_btn);
        profile=findViewById(R.id.qwe);
        recyclerView = findViewById(R.id.chat_recylerview);
        recyclerView.setVisibility(View.GONE);
        imageView=findViewById(R.id.imageviewprofile);
        username=findViewById(R.id.chat_txtview);
        messageList = new ArrayList<>();
        chat_toolbar.setVisibility(View.INVISIBLE);
        bottom_layout.setVisibility(View.INVISIBLE);
        String senderUid = FirebaseAuth.getInstance().getUid();
        String receiverUid = getIntent().getStringExtra("userid");

        if (TextUtils.isEmpty(senderUid) ||  TextUtils.isEmpty(receiverUid)) {
            Log.e("ChatActivity", "Sender UID or Receiver UID is null.");
            Toast.makeText(this, "Failed to load chat. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        chatAdapter = new ChatAdapter(this, messageList,senderRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);




        chatrefrence= FirebaseDatabase.getInstance().getReference().child("chatss");
        reference = FirebaseDatabase.getInstance().getReference().child("chatsRooms").child(senderRoom);
        useref=FirebaseDatabase.getInstance().getReference().child("users").child(receiverUid);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this,ProfileActivity.class);
                intent.putExtra("userid",receiverUid);
                startActivity(intent);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel message = snapshot1.getValue(MessageModel.class);
                    messageList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
                if (messageList.size() > 0) {
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
                fetchuserdetails();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = txt.getText().toString();
                if (!messageText.isEmpty()) {
                    MessageModel message = new MessageModel(messageText, senderUid, System.currentTimeMillis());
                    txt.setText("");

                    reference.child(reference.push().getKey()).setValue(message);
                    FirebaseDatabase.getInstance().getReference().child("chatsRooms").child(receiverRoom)
                            .child(reference.push().getKey()).setValue(message);

                    User userModel1 = new User(userModel.getName(), receiverUid);
                    chatrefrence.child(FirebaseAuth.getInstance().getUid()).child(receiverUid).setValue(userModel1);


                    FirebaseDatabase.getInstance().getReference().child("activeUsers").child(receiverUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean isActive = snapshot.exists() && snapshot.getValue(Boolean.class);
                                    if (!isActive) {

                                        chatrefrence.child(receiverUid).child(FirebaseAuth.getInstance().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            int currentCount = 0;
                                                            if (snapshot.child("messagecount").getValue() != null) {
                                                                currentCount = snapshot.child("messagecount").getValue(Integer.class);
                                                            }
                                                            int newCount = currentCount + 1;
                                                            DatabaseReference countRef = chatrefrence.child(receiverUid)
                                                                    .child(FirebaseAuth.getInstance().getUid()).child("messagecount");
                                                            countRef.setValue(newCount);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
            }
        });


    }

    private void fetchuserdetails() {
        useref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   userModel =snapshot.getValue(User.class);
                    if(userModel !=null){
                        username.setText(userModel.getName());
                        String imageKey= userModel.getProfile();
                        if (!TextUtils.isEmpty(imageKey)) {
                            int imageResId = getResources().getIdentifier(imageKey, "drawable", getPackageName());
                            if (imageResId != 0) {
                                imageView.setImageResource(imageResId);
                            } else {
                                imageView.setImageResource(R.drawable.unknownprofile);
                            }
                        } else {
                            imageView.setImageResource(R.drawable.
                                    unknownprofile);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    chat_toolbar.setVisibility(View.VISIBLE);
                    bottom_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the current user as active
        String currentUserUid = FirebaseAuth.getInstance().getUid();
        if (!TextUtils.isEmpty(currentUserUid)) {
            FirebaseDatabase.getInstance().getReference().child("activeUsers")
                    .child(currentUserUid).setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set the current user as inactive
        String currentUserUid = FirebaseAuth.getInstance().getUid();
        if (!TextUtils.isEmpty(currentUserUid)) {
            FirebaseDatabase.getInstance().getReference().child("activeUsers")
                    .child(currentUserUid).setValue(false);
        }
    }
}