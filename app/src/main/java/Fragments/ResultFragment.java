package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
//lavadi
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.RapCodeTechnologies.Quiz.UserMainActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";
    boolean coinadded=false;
    private String userId;
    private ProgressBar loadingProgressBar;
    private static final String ARG_TOTAL_QUESTIONS = "total_questions";
    private static final String ARG_USER_ID = "user_id";

    private LottieAnimationView animationView;
    private ScrollView scrollView;
    private LottieAnimationView animationViewSpattered;
    private Button backToMenuBtn, startAgainBtn;
    private TextView resultTextView, resultUsername, resultCoinTextView,eranedcoin,addingcoin;
    private DatabaseReference databaseReference;

    public static ResultFragment newInstance(int score, int totalQuestions, String userId) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        resultTextView = view.findViewById(R.id.resultTextView);
        resultUsername = view.findViewById(R.id.resultusername);
        backToMenuBtn=view.findViewById(R.id.backtomenu);
        startAgainBtn=view.findViewById(R.id.stagain);
        eranedcoin=view.findViewById(R.id.eranedcointext);
        resultCoinTextView = view.findViewById(R.id.resultcoin);
        loadingProgressBar=view.findViewById(R.id.loadingProgressBar);
        scrollView=view.findViewById(R.id.scvieww);
        loadingProgressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        animationView = view.findViewById(R.id.lottieanimationcoin);
        addingcoin=view.findViewById(R.id.addingcoin);
        ConstraintLayout.LayoutParams progressBarLayoutParams = (ConstraintLayout.LayoutParams) loadingProgressBar.getLayoutParams();
        progressBarLayoutParams.horizontalBias = 0.5f;
        progressBarLayoutParams.verticalBias = 0.5f;
        loadingProgressBar.setLayoutParams(progressBarLayoutParams);

        animationViewSpattered = view.findViewById(R.id.lottieanimationcoinspattered);
        animationView = view.findViewById(R.id.lottieanimationcoin);

        addingcoin = view.findViewById(R.id.addingcoin);

        animationViewSpattered.setAnimation(R.raw.coinanimation);
        animationView.setAnimation(R.raw.coinanimation);



        animationViewSpattered.setRepeatCount(-1);
        animationView.setRepeatCount(-1);



        animationViewSpattered.playAnimation();
        animationView.playAnimation();


        startAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCoinsAddedFlag();
                restartQuiz();
            }
        });

        backToMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCoinsAddedFlag();
                navigateToMainActivity();
            }
        });


        if (getArguments() != null) {
            int score = getArguments().getInt(ARG_SCORE);
            int totalQuestions = getArguments().getInt(ARG_TOTAL_QUESTIONS);
            userId = getArguments().getString(ARG_USER_ID);

            double percentage = (score * 100.0) / totalQuestions;
            int coins = calculateCoins(percentage);

            resultTextView.setText("You scored " + score + " out of " + totalQuestions);
            resultCoinTextView.setText(String.valueOf(coins));


            fetchUserData(userId,coins);
            handleBackPress();
        }

        return view;
    }
    private void restartQuiz() {

        Intent intent = new Intent(getActivity(), QuizActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private int calculateCoins(double percentage) {
        if (percentage == 100) {
            return 20;
        } else if (percentage >= 95 && percentage <= 99) {
            return 18;
        } else if (percentage >= 91 && percentage <= 95) {
            return 15;
        } else if (percentage >= 81 && percentage <= 90) {
            return 10;
        } else if (percentage >= 70 && percentage <= 79) {
            return 5;
        } else {
            return 0;
        }
    }
    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                resetCoinsAddedFlag();
                navigateToMainActivity();
            }
        });
    }

    private void fetchUserData(String userId, int coins) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String name = dataSnapshot.child("name").getValue(String.class);

                    Integer currentCoins = dataSnapshot.child("coin").getValue(Integer.class);
                    Boolean added = dataSnapshot.child("coinsAddedForQuiz").getValue(Boolean.class);

                    if (currentCoins == null) {
                        currentCoins = 0;
                    }
                    if(added==null){
                        added=false;
                    }
                    else {
                        added=true;
                    }
                    if (!added) {
                        int finalCoins = currentCoins + coins;
                        databaseReference.child("coin").setValue(finalCoins);
                        added = true;
                        databaseReference.child("coinsAddedForQuiz").setValue(true);
                    }
                    eranedcoin.setText("You Earned " + coins + " Coins!!");
                    resultCoinTextView.setText(String.valueOf(currentCoins));
                    addingcoin.setText(" + " + coins);

                    resultUsername.setText(name);
                    loadingProgressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);


                } else {
                    Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadingProgressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navigateToMainActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), UserMainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetCoinsAddedFlag();
    }
    @Override
    public void onPause() {
        super.onPause();
        resetCoinsAddedFlag();
    }
    private void resetCoinsAddedFlag() {
        if (userId != null) {
            databaseReference.child("coinsAddedForQuiz").removeValue();
        }
    }


}
