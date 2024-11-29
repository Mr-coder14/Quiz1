package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adaptors.RequestAdaptor;
import Models.User;


public class BookMarkFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference requests;
    private ArrayList<User> arrayList;
    private String userid;
    private ProgressBar progressBar;
    private RequestAdaptor adaptor;



    public BookMarkFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_book_mark, container, false);
        userid= FirebaseAuth.getInstance().getUid();
        recyclerView=view.findViewById(R.id.requestsrecyculer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar=view.findViewById(R.id.progressBarbook);
        requests= FirebaseDatabase.getInstance().getReference().child("requests").child(userid);
        arrayList=new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        fetchdetails();
        adaptor=new RequestAdaptor(arrayList);
        recyclerView.setAdapter(adaptor);

        return view;
    }

    private void fetchdetails() {
        requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot usere:snapshot.getChildren()){
                        User user=usere.getValue(User.class);
                        arrayList.add(user);
                    }

                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(() -> {
                        adaptor.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);// Notify adapter

                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}