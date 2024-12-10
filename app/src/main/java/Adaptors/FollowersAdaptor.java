package Adaptors;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.FollowersActivity;
import com.RapCodeTechnologies.Quiz.ProfileActivity;
import com.RapCodeTechnologies.Quiz.R;

import java.util.ArrayList;

import Models.User;

public class FollowersAdaptor extends RecyclerView.Adapter<FollowersAdaptor.viewgolwe> {
    private ArrayList<User> arrayList;
    private Context context;
    public FollowersAdaptor(ArrayList<User> arrayList, Context context)
    {
        this.arrayList=arrayList;
        this.context=context;
    }
    @NonNull
    @Override
    public FollowersAdaptor.viewgolwe onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.userlisttemplate, parent, false);
        return new FollowersAdaptor.viewgolwe(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersAdaptor.viewgolwe holder, int position) {
              User user=arrayList.get(position);
              holder.bind(user);
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

    public class viewgolwe extends RecyclerView.ViewHolder {
        private ImageView profile;
        private TextView textView;
        public viewgolwe(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.profilepicuserlist);
            textView=itemView.findViewById(R.id.user_profile_name);
        }

        public void bind(User user) {
            textView.setText(user.getName());
            String imageKey=user.getProfile();
            if (!TextUtils.isEmpty(imageKey)) {
                int imageResId = context.getResources().getIdentifier(imageKey, "drawable", context.getPackageName());
                if (imageResId != 0) {
                    profile.setImageResource(imageResId);
                } else {
                    profile.setImageResource(R.drawable.unknownprofile);
                }
            } else {
                profile.setImageResource(R.drawable.unknownprofile);
            }

        }
    }
}
