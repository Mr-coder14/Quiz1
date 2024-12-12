package Adaptors;



import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.RapCodeTechnologies.Quiz.ChatActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Models.User;


public class ChatlistAdaptor extends RecyclerView.Adapter<ChatlistAdaptor.ViewHolder> {
    private ArrayList<User> userModels;
    private Context context;
    private String userid;



    public ChatlistAdaptor(Context context, ArrayList<User> userModels,String userid){
        this.context=context;
        this.userModels=userModels;
        this.userid=userid;
    }
    @NonNull
    @Override
    public ChatlistAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.userlisttemplate,parent,false);
        return new ChatlistAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatlistAdaptor.ViewHolder holder, int position) {
        User userModel=userModels.get(position);
        holder.bind(userModel);
        DatabaseReference userChatRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("chatss")
                .child(userid)
                .child(userModel.getUserid())
                .child("messagecount");
        userChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int liveMessageCount = snapshot.getValue(Integer.class);
                    if (String.valueOf(liveMessageCount) != null && liveMessageCount > 0) {
                        holder.l.setVisibility(View.VISIBLE);
                        holder.messagecount.setText(String.valueOf(liveMessageCount));
                    } else {
                        holder.l.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors here
            }
        });
        DatabaseReference lastMessageRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("chatsRooms")
                .child(userid + userModel.getUserid());

        lastMessageRef.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        Long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                        if (timestamp != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            String formattedTime = sdf.format(new Date(timestamp));
                            holder.timestampLastMessage.setText(formattedTime);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=0;
                Intent intent=new Intent(context, ChatActivity.class);

                userChatRef.setValue(0);

                intent.putExtra("userid",userModel.getUserid());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,messagecount,timestampLastMessage;
        private ShapeableImageView imageView;
        private LinearLayout l;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.user_profile_name);
            l=itemView.findViewById(R.id.countmessagebg);
            messagecount=itemView.findViewById(R.id.messagecount);
            imageView=itemView.findViewById(R.id.profilepicuserlist);
            timestampLastMessage=itemView.findViewById(R.id.timestamplastmessage);

        }
        public void bind(User userModel){
           name.setText(userModel.getName());
            if(String.valueOf(userModel.getMessagecount())!=null && userModel.getMessagecount()!=0){
                l.setVisibility(View.VISIBLE);
                messagecount.setText(String.valueOf(userModel.getMessagecount()));
            }

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(userModel.getUserid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User u=snapshot.getValue(User.class);
                        String imageKey = u.getProfile();
                        if (!TextUtils.isEmpty(imageKey)) {
//                            name.setText(u.getName());
                            int imageResId = context.getResources().getIdentifier(imageKey, "drawable", context.getPackageName());
                            if (imageResId != 0) {
                                imageView.setImageResource(imageResId);
                            } else {
                                imageView.setImageResource(R.drawable.unknownprofile);

                            }
                        } else {
                            imageView.setImageResource(R.drawable.unknownprofile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }
}