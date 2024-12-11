package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adaptors.CategoryAdapter;
import Adaptors.RecentQuizesAdaptor;
import Adaptors.RequestAdaptor;
import Models.User;

public class BookMarkFragment extends Fragment {
    private RecyclerView recyclerView, savedquizzes,recyclerViews;
    private DatabaseReference requests, savedquiz,databaseReference;
    private ArrayList<User> requestList;
    private ArrayList<String> arrayList;
    private String userid;
    private ArrayList<String> savedQuizList;
    private TextView requestedtxr,fee;
    private ProgressBar progressBar;
    private RequestAdaptor requestAdaptor;
    private RecentQuizesAdaptor savedQuizAdaptor;
    private CategoryAdapter adapter;

    public BookMarkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_mark, container, false);
        userid = FirebaseAuth.getInstance().getUid();

        recyclerView = view.findViewById(R.id.requestsrecyculer);
        savedquizzes = view.findViewById(R.id.savedquizrecyculer);
        requestedtxr=view.findViewById(R.id.requestedtxr);
        recyclerViews=view.findViewById(R.id.recyculerfe);
        fee=view.findViewById(R.id.fee);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedquizzes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViews.setLayoutManager(new GridLayoutManager(getContext(), 2));


        progressBar = view.findViewById(R.id.progressBarbook);
        requests = FirebaseDatabase.getInstance().getReference().child("requests").child(userid);
        savedquiz = FirebaseDatabase.getInstance().getReference().child("savedQuizzes").child(userid);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("quizzes");

        requestList = new ArrayList<>();
        savedQuizList = new ArrayList<>();
        arrayList=new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        requestedtxr.setVisibility(View.GONE);
        savedquizzes.setVisibility(View.GONE);
        recyclerViews.setVisibility(View.GONE);
        fee.setVisibility(View.GONE);

        requestAdaptor = new RequestAdaptor(requestList);
        savedQuizAdaptor = new RecentQuizesAdaptor(savedQuizList, getContext());
        adapter = new CategoryAdapter(getContext(), arrayList);



        recyclerView.setAdapter(requestAdaptor);
        savedquizzes.setAdapter(savedQuizAdaptor);
        recyclerViews.setAdapter(adapter);

        fetchRequests();
        fetchSavedQuizzes();
        fetchcatogareis();

        return view;
    }
    private void fetchcatogareis() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(dataSnapshot.getKey()!=null){
                            arrayList.add(dataSnapshot.getKey());
                        }
                    }
                    checkLoadingState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkLoadingState();

            }
        });
    }

    private void fetchRequests() {
        requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            requestList.add(user);
                        }
                    }
                }
                checkLoadingState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkLoadingState();
            }
        });
    }

    private void fetchSavedQuizzes() {
        savedquiz.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                        String quizId = quizSnapshot.getKey();
                        if (quizId != null) {
                            savedQuizList.add(quizId);
                        }
                    }
                }
                checkLoadingState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkLoadingState();
            }
        });
    }

    private void checkLoadingState() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (!requestList.isEmpty() || !savedQuizList.isEmpty() || !arrayList.isEmpty()) {
                    requestAdaptor.notifyDataSetChanged();
                    savedQuizAdaptor.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    savedquizzes.setVisibility(View.VISIBLE);
                    recyclerViews.setVisibility(View.VISIBLE);
                    if(requestList.size()>0){
                        requestedtxr.setVisibility(View.VISIBLE);
                    }
                    if(savedQuizList.size()>0){
                        requestedtxr.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
