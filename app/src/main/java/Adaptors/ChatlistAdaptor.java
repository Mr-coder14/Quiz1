package Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.RapCodeTechnologies.Quiz.ChatActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=0;
                Intent intent=new Intent(context, ChatActivity.class);

                userChatRef.setValue(0);

                intent.putExtra("usermodel",userModel);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,phno,messagecount;
        private LinearLayout l;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.user_profile_name);
            l=itemView.findViewById(R.id.countmessagebg);
            messagecount=itemView.findViewById(R.id.messagecount);

        }
        public void bind(User userModel){
            name.setText(userModel.getName());
            if(String.valueOf(userModel.getMessagecount())!=null && userModel.getMessagecount()!=0){
                l.setVisibility(View.VISIBLE);
                messagecount.setText(String.valueOf(userModel.getMessagecount()));
            }

        }
    }
}