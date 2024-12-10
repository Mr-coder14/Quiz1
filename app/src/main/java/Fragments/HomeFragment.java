package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.AllQuizActivity;
import com.RapCodeTechnologies.Quiz.MessageListActivity;
import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adaptors.BannerAdapter;
import Models.BannerItem;
import Models.User;

public class HomeFragment extends Fragment implements BannerAdapter.OnBannerClickListener {
    private TextView layout;
    private ImageView imageView,profileimage;
    private TextView username,coinhome,seeallquizzes;
    private ViewPager2  bannerViewPager;
    private static final int BANNER_DELAY_MS=2000;
    private DatabaseReference databaseReference;
    private String userid;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    public HomeFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        layout=view.findViewById(R.id.quiztimee);
        imageView=view.findViewById(R.id.messager);
        coinhome=view.findViewById(R.id.oinshome);
        seeallquizzes=view.findViewById(R.id.seeallquizzes);
        username=view.findViewById(R.id.homeusername);
        bannerViewPager=view.findViewById(R.id.bannerViewPager);
        profileimage=view.findViewById(R.id.imageView3);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MessageListActivity.class));
            }
        });
        seeallquizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllQuizActivity.class));
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), QuizActivity.class));
            }
        });
        fetuserdetails();
        loadBanners();
        setupAutoSwipeBanners();
        return view;
    }
    private void loadBanners() {
        List<BannerItem> banners = new ArrayList<>();
        banners.add(new BannerItem("Combo Offer!", "Blue Pen-3,Black-1", "Buy Now", Color.parseColor("#FFE4E1"), R.drawable.cat1));
        banners.add(new BannerItem("Combo Offer!", "Tip Pencil,Box,Scale,Eraser", "Shop Now", Color.parseColor("#E1F5FE"), R.drawable.cat2));
        banners.add(new BannerItem("Drafter Combo!", "Drafter,A3 Note", "Get Now", Color.parseColor("#f0df60"), R.drawable.cat3));
        banners.add(new BannerItem("Order Book!","Order your Favourite Book now!","Get Book",Color.parseColor("#FFE4E1"),R.drawable.cat4));
        banners.add(new BannerItem("Order products!","Get the Products for Projects!","Get Products",Color.parseColor("#E1F5FE"),R.drawable.technology));


        BannerAdapter bannerAdapter = new BannerAdapter(banners, this);
        bannerViewPager.setAdapter(bannerAdapter);
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
    private void setupAutoSwipeBanners() {

        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (bannerViewPager.getCurrentItem() + 1) % bannerViewPager.getAdapter().getItemCount();
                bannerViewPager.setCurrentItem(nextItem, true);
                bannerHandler.postDelayed(this, BANNER_DELAY_MS);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
}

    @Override
    public void onBannerClick(BannerItem bannerItem) {
        Intent intent=null;
        switch (bannerItem.getButtonText()) {
            case "Buy Now":
//                intent = new Intent(getActivity(), ComboOfferpen.class);
                break;
            case "Shop Now":
//                intent = new Intent(getActivity(), Combopencil.class);
                break;
            case "Get Book":
//                intent=new Intent(getActivity(), BookFormApplication.class);
                break;
            case "Get Products":
//                intent=new Intent(getActivity(), Projectproductsfrormapplication.class);
                break;
            default:
//                intent = new Intent(getActivity(), ComboDrafter.class);
                break;
        }
        startActivity(intent);

    }
}