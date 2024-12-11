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
import com.RapCodeTechnologies.Quiz.QuizLevelActivity;
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
    private LinearLayout imageView,cpp,py,web,app;
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
        cpp=view.findViewById(R.id.cpp);
        web=view.findViewById(R.id.web_dev);
        py=view.findViewById(R.id.python);
        app=view.findViewById(R.id.app_dev);
        seeallquizzes=view.findViewById(R.id.seeallquizzes);
        dlayout=view.findViewById(R.id.homeee);
        progressBar.setVisibility(View.VISIBLE);
        dlayout.setVisibility(View.GONE);
        username=view.findViewById(R.id.homeusername);
        bannerViewPager=view.findViewById(R.id.bannerViewPager);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        chatsReference = FirebaseDatabase.getInstance().getReference().child("chatss").child(userid);

        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openquiz("Android Development");
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openquiz("Web Development");
            }
        });
        cpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openquiz("C++ ");
            }
        });
        py.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openquiz("Python");
            }
        });
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
                startActivity(new Intent(getContext(), AllQuizActivity.class));
            }
        });
        loadBanners();
        setupAutoSwipeBanners();
        fetchTotalMessageCount();
        return view;
    }

    private void openquiz(String name) {
        Intent intent=new Intent(getContext(), QuizLevelActivity.class);
        intent.putExtra("name",name);
        getActivity().startActivity(intent);
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
        banners.add(new BannerItem("Web Development!", "Attend Quiz!!", "Earn Coins!", Color.parseColor("#FFE4E1"), R.drawable.webb));
        banners.add(new BannerItem("Android Development", "Attend Quiz!!", "Earn Coins!", Color.parseColor("#E1F5FE"), R.drawable.androoid));
        banners.add(new BannerItem("Java", "Attend Quiz!!", "Earn Coins!", Color.parseColor("#f0df60"), R.drawable.java));
        banners.add(new BannerItem("C Programming","Attend Quiz!!","Earn Coins!",Color.parseColor("#FFE4E1"),R.drawable.cprogramming));
        banners.add(new BannerItem("Php Programming","Attend Quiz!!","Earn Coins!",Color.parseColor("#E1F5FE"),R.drawable.phpp));


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
        switch (bannerItem.getTitle()) {
            case "Php Programming":
                intent=new Intent(getContext(), QuizLevelActivity.class);
                intent.putExtra("name","Php Programming");
                break;
            case "C Programming":
                intent=new Intent(getContext(), QuizLevelActivity.class);
                intent.putExtra("name","C Programming");

                break;
            case "Java":
                intent=new Intent(getContext(), QuizLevelActivity.class);
                intent.putExtra("name","Java");

                break;
            case "Android Development":
                 intent=new Intent(getContext(), QuizLevelActivity.class);
                intent.putExtra("name","Android Development");

                break;
            default:

                intent=new Intent(getContext(), QuizLevelActivity.class);
                intent.putExtra("name","Web Development");
                break;

        }
        if(intent!=null){
            getActivity().startActivity(intent);
        }


    }
}