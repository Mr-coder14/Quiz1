package Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.RapCodeTechnologies.Quiz.ChatActivity;
import com.RapCodeTechnologies.Quiz.R;

import java.util.ArrayList;

import Models.User;


public class searchadaptor extends RecyclerView.Adapter<searchadaptor.viewholer> {
    private ArrayList<User> userModels;
    private Context context;

    public searchadaptor(Context context,ArrayList<User> userModels){
        this.context=context;
        this.userModels=userModels;
    }
    @NonNull
    @Override
    public searchadaptor.viewholer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.userlisttemplate,parent,false);
        return new searchadaptor.viewholer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchadaptor.viewholer holder, int position) {
        User userModel=userModels.get(position);
        holder.bind(userModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("usermodel",userModel);
                intent.putExtra("userid",userModel.getUserid());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class viewholer extends RecyclerView.ViewHolder {
        private TextView name,phno;
        public viewholer(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.user_profile_name);
        }
        public void bind(User userModel){
            name.setText(userModel.getName());

        }
    }
}
