package Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.R;

import java.util.ArrayList;

import Models.LeaderBoard;

public class LeadersAdaptor extends RecyclerView.Adapter<LeadersAdaptor.viewholder> {
    private ArrayList<LeaderBoard> leaderBoards;
    public LeadersAdaptor(ArrayList<LeaderBoard> leaderBoards){
        this.leaderBoards=leaderBoards;
    }
    @NonNull
    @Override
    public LeadersAdaptor.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_item,parent,false);
        return new LeadersAdaptor.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadersAdaptor.viewholder holder, int position) {
        LeaderBoard leaderBoard=leaderBoards.get(position);
        holder.bind(leaderBoard);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return leaderBoards.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        private TextView username,no,coin;
        private ImageView profileimg;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.nthplaceusername);
            no=itemView.findViewById(R.id.nno);
            coin=itemView.findViewById(R.id.nthhplacecoin);
            profileimg=itemView.findViewById(R.id.nthplaceimageview);
        }

        public void bind(LeaderBoard leaderBoard) {
            username.setText(leaderBoard.getUsername());
            no.setText(String.valueOf(leaderBoard.getNo()));
            coin.setText(String.valueOf(leaderBoard.getCoin()));
        }
    }
}
