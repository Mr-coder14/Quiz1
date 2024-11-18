package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Fragments.BookMarkFragment;
import Fragments.HomeFragment;
import Fragments.LeaderFragment;
import Fragments.ProfileFragment;
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment= new HomeFragment();
        bottomNavigationView=findViewById(R.id.bottomappbar);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id=menuItem.getItemId();
                if(id==R.id.Homebottom){
                    fragment =new HomeFragment();
                }
                if(id==R.id.BoardBottom){
                    fragment =new LeaderFragment();
                }
                if(id==R.id.BookMarkBottom){
                    fragment =new BookMarkFragment();
                }
                if(id==R.id.ProfileBottom){
                    fragment =new ProfileFragment();
                }

                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                    return true;
                }else {
                    return false;
                }
            }
        });
        deleteCoinsAddedForQuiz();

        }
    private void deleteCoinsAddedForQuiz() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        DatabaseReference userRef = databaseReference.child(userId);


                        userRef.child("coinsAddedForQuiz").removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                    } else {

                                    }
                                });
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
    }
