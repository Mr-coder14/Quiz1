package Adaptors;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.R;

import java.util.ArrayList;
import java.util.List;

import Models.User;

public class RecentQuizzesAdaptor extends RecyclerView.Adapter<RecentQuizzesAdaptor.viewHolder> {
    private List<Boolean> recentQuizzes;
    private Context context;
    private String userid;
    @NonNull
    @Override
    public RecentQuizzesAdaptor.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_quizzestemplate,parent,false);
        return new RecentQuizzesAdaptor.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentQuizzesAdaptor.viewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return recentQuizzes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title,total;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.quiztitled);
            total=itemView.findViewById(R.id.totalques);
            img=itemView.findViewById(R.id.imgrecentquizes);
        }
    }
}
