package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.MessageListActivity;
import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.User;

public class HomeFragment extends Fragment {
    private LinearLayout layout;
    private ImageView imageView,profileimage;
    private TextView username,stnow,coinhome;
    private DatabaseReference databaseReference;
    private String userid;

    public HomeFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        layout=view.findViewById(R.id.quiztimee);
        imageView=view.findViewById(R.id.messager);
        stnow=view.findViewById(R.id.startnowcoin);
        coinhome=view.findViewById(R.id.oinshome);
        username=view.findViewById(R.id.homeusername);
        profileimage=view.findViewById(R.id.imageView3);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MessageListActivity.class));
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), QuizActivity.class));
            }
        });
        fetuserdetails();
        return view;
    }

    private void fetuserdetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    username.setText("Hi, "+user.getName());
                    coinhome.setText(String.valueOf(user.getCoin()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}