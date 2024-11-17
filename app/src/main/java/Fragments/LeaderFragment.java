package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import Adaptors.LeadersAdaptor;
import Models.LeaderBoard;


public class LeaderFragment extends Fragment {
    private ArrayList<LeaderBoard> leaderBoards;
    private RecyclerView recyclerView;
    private LeadersAdaptor adaptor;
    private String userid;

    public LeaderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_leader, container, false);
        recyclerView=view.findViewById(R.id.recyculerleaders);
        leaderBoards = new ArrayList<>();
        userid=FirebaseAuth.getInstance().getUid();
        leaderBoards.add(new LeaderBoard("Parmugilan S","2500", userid,4));
        leaderBoards.add(new LeaderBoard("Shyam S","2600",userid,5));
        leaderBoards.add(new LeaderBoard("Navneethan S","2700", userid,6));
        leaderBoards.add(new LeaderBoard("Sanjith S","2800",userid,7));
        leaderBoards.add(new LeaderBoard("Jeeva S","2900", userid,8));
        leaderBoards.add(new LeaderBoard("Sangavi S","2600",userid,9));
        leaderBoards.add(new LeaderBoard("Jeeva S","2400", userid,10));
        leaderBoards.add(new LeaderBoard("Snjay S","2300", userid,11));
        leaderBoards.add(new LeaderBoard("Dhamotharan S","2500", userid,12));
        leaderBoards.add(new LeaderBoard("Eajesh S","2200",userid,13));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptor=new LeadersAdaptor(leaderBoards);
        recyclerView.setAdapter(adaptor);
        return view;
    }
}