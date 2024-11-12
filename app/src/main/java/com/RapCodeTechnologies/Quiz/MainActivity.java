package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import Fragments.BookMarkFragment;
import Fragments.HomeFragment;
import Fragments.LeaderFragment;
import Fragments.ProfileFragment;
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment= new HomeFragment();
        bottomNavigationView=findViewById(R.id.bottomappbar);

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

        }
    }
