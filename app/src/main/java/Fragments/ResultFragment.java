package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.R;


public class ResultFragment extends Fragment {


    private static final String ARG_SCORE = "score";
    private static final String ARG_TOTAL_QUESTIONS = "total_questions";

    public static ResultFragment newInstance(int score, int totalQuestions) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_result, container, false);
        TextView resultTextView = view.findViewById(R.id.resultTextView);

        if (getArguments() != null) {
            int score = getArguments().getInt(ARG_SCORE);
            int totalQuestions = getArguments().getInt(ARG_TOTAL_QUESTIONS);
            resultTextView.setText("You scored " + score + " out of " + totalQuestions);
        }
        return view;
    }
}