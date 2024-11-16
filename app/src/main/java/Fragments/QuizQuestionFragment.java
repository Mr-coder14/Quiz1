package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.android.material.snackbar.Snackbar;


public class QuizQuestionFragment extends Fragment {
    private TextView questionTextView;
    private Button optionA, optionB, optionC, optionD;
    private String correctAnswer;
    private OnCorrectAnswerListener onCorrectAnswerListener;

    public interface OnCorrectAnswerListener {
        void onCorrectAnswer();
    }

    public void setOnCorrectAnswerListener(OnCorrectAnswerListener listener) {
        this.onCorrectAnswerListener = listener;
    }

    public QuizQuestionFragment() {

    }
    public static QuizQuestionFragment newInstance(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("optionA", optionA);
        args.putString("optionB", optionB);
        args.putString("optionC", optionC);
        args.putString("optionD", optionD);
        args.putString("correctAnswer", correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTextView = view.findViewById(R.id.questionTextView);
        optionA = view.findViewById(R.id.optionA);
        optionB = view.findViewById(R.id.optionB);
        optionC = view.findViewById(R.id.optionC);
        optionD = view.findViewById(R.id.optionD);

        if (getArguments() != null) {
            questionTextView.setText(getArguments().getString("question"));
            optionA.setText(getArguments().getString("optionA"));
            optionB.setText(getArguments().getString("optionB"));
            optionC.setText(getArguments().getString("optionC"));
            optionD.setText(getArguments().getString("optionD"));
            correctAnswer = getArguments().getString("correctAnswer");
        }

        optionA.setOnClickListener(v -> checkAnswer(optionA.getText().toString()));
        optionB.setOnClickListener(v -> checkAnswer(optionB.getText().toString()));
        optionC.setOnClickListener(v -> checkAnswer(optionC.getText().toString()));
        optionD.setOnClickListener(v -> checkAnswer(optionD.getText().toString()));

        return view;
    }
    private void checkAnswer(String selectedAnswer) {
        boolean isCorrect=false;
        if (selectedAnswer.equals(correctAnswer)) {
            isCorrect = true;

            showSnackbar(isCorrect);

            if (onCorrectAnswerListener != null) {
                onCorrectAnswerListener.onCorrectAnswer();
            }
            ((QuizActivity) getActivity()).loadNextQuestion();
        } else {
            showSnackbar(isCorrect);
            ((QuizActivity) getActivity()).loadNextQuestion();
        }
    }
    private void showSnackbar(boolean isCorrect) {
        String message = isCorrect
                ? "Correct Answer!"
                : "Wrong! ";

        int color = isCorrect ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#F44336"); // Green and Red

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();

        // Apply background color
        snackbarView.setBackgroundColor(color);

        // Show the Snackbar
        snackbar.show();
    }

}