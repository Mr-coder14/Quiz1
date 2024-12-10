package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.RapCodeTechnologies.Quiz.AllQuizActivity;
import com.RapCodeTechnologies.Quiz.MessageListActivity;
import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.android.material.imageview.ShapeableImageView;
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
    private LinearLayout imageView;
    private TextView username,coinhome,seeallquizzes;
    private ShapeableImageView shapeableImageView;
    private ViewPager2  bannerViewPager;
    private static final int BANNER_DELAY_MS=2000;
    private DatabaseReference databaseReference;
    private String userid;
    private Handler bannerHandler;
    private DatabaseReference chatsReference;
    private TextView messageCountBadge;
    private Runnable bannerRunnable;
    private ProgressBar progressBar;
    private LinearLayout dlayout;
    public HomeFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        layout=view.findViewById(R.id.quiztimee);
        imageView=view.findViewById(R.id.messager);
        coinhome=view.findViewById(R.id.oinshome);
        progressBar=view.findViewById(R.id.progressbarhome);
        messageCountBadge = view.findViewById(R.id.messagecounttoatal);
        shapeableImageView=view.findViewById(R.id.imageView3);
        seeallquizzes=view.findViewById(R.id.seeallquizzes);
        dlayout=view.findViewById(R.id.homeee);
        progressBar.setVisibility(View.VISIBLE);
        dlayout.setVisibility(View.GONE);
        username=view.findViewById(R.id.homeusername);
        bannerViewPager=view.findViewById(R.id.bannerViewPager);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        chatsReference = FirebaseDatabase.getInstance().getReference().child("chatss").child(userid);
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
        loadBanners();
        setupAutoSwipeBanners();
        fetchTotalMessageCount();
        return view;
    }
    private void fetchTotalMessageCount() {
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) {
                    return; // Fragment is not attached to activity
                }

                int totalMessageCount = 0;

                // Iterate through each child and sum up message counts
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    Long messageCount = chatSnapshot.child("messagecount").getValue(Long.class);
                    if (messageCount != null) {
                        totalMessageCount += messageCount;
                    }
                }

                // Update the UI
                if (totalMessageCount > 0) {
                    messageCountBadge.setText(String.valueOf(totalMessageCount));
                    messageCountBadge.setVisibility(View.VISIBLE);
                } else {
                    messageCountBadge.setVisibility(View.GONE);
                }
                fetuserdetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    return; // Fragment is not attached to activity
                }

                Toast.makeText(getContext(), "Failed to fetch message count.", Toast.LENGTH_SHORT).show();
            }
        });
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
                if (!isAdded()) {
                    // Fragment is not attached to the activity, so return early
                    return;
                }

                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        username.setText("Hi, " + user.getName());
                        coinhome.setText(String.valueOf(user.getCoin()));
                        String imageKey = user.getProfile();
                        if (!TextUtils.isEmpty(imageKey)) {
                            int imageResId = getResources().getIdentifier(imageKey, "drawable", requireContext().getPackageName());
                            if (imageResId != 0) {
                                shapeableImageView.setImageResource(imageResId);
                            } else {
                                shapeableImageView.setImageResource(R.drawable.unknownprofile);
                            }
                        } else {
                            shapeableImageView.setImageResource(R.drawable.unknownprofile);
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                dlayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) {
                    // Fragment is not attached to the activity, so return early
                    return;
                }

                Toast.makeText(getContext(), "Failed to load user details. Please try again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dlayout.setVisibility(View.VISIBLE);
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