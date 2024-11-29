package Adaptors;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.ProfileActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Models.User;

public class RequestAdaptor extends RecyclerView.Adapter<RequestAdaptor.viewHolder> {
    private Context context;
    private ArrayList<User> arrayList;
    private DatabaseReference followers,requests;
    private String usersid;
    private FirebaseAuth auth;
    public RequestAdaptor(ArrayList<User> arrayList){
        this.arrayList=arrayList;
        auth=FirebaseAuth.getInstance();
        usersid=auth.getCurrentUser().getUid();
        followers= FirebaseDatabase.getInstance().getReference().child("followers").child(usersid);
        requests=FirebaseDatabase.getInstance().getReference().child("requests").child(usersid);
    }
    @NonNull
    @Override
    public RequestAdaptor.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.requesttemplate,parent,false);
        return new RequestAdaptor.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdaptor.viewHolder holder, int position) {
            User user=arrayList.get(position);
            holder.bind(user);
            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followers.child(user.getUserid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                requests.child(user.getUserid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        }
                    });

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requests.child(user.getUserid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                Toast.makeText(context, "Requested Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ProfileActivity.class);
                    intent.putExtra("userid",user.getUserid());
                    context.startActivity(intent);
                }

                });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView username;
        private Button confirm,delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            confirm=itemView.findViewById(R.id.confirmrequest);
            delete=itemView.findViewById(R.id.deleterequest);
            username=itemView.findViewById(R.id.user_profile_name);

        }
        public void bind(User user){
              username.setText(user.getName());
        }

    }
}
