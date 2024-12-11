package Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.RapCodeTechnologies.Quiz.QuizActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import javax.sql.DataSource;


public class QuizQuestionFragment extends Fragment {
    private TextView questionTextView;
    private Button optionA, optionB, optionC, optionD;
    private String correctAnswer;

    private ImageView imageView;
    private ProgressBar questionLoadingProgressBar;
    private OnCorrectAnswerListener onCorrectAnswerListener;

    public interface OnCorrectAnswerListener {
        void onCorrectAnswer();
    }

    public void setOnCorrectAnswerListener(OnCorrectAnswerListener listener) {
        this.onCorrectAnswerListener = listener;
    }

    public QuizQuestionFragment() {

    }
    public static QuizQuestionFragment newInstance(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer,int questionIndex) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("optionA", optionA);
        args.putString("optionB", optionB);
        args.putString("optionC", optionC);
        args.putString("optionD", optionD);
        args.putString("correctAnswer", correctAnswer);
        args.putInt("questionIndex", questionIndex);
        fragment.setArguments(args);
        return fragment;
    }
    private OnImageLoadListener onImageLoadListener;

    public interface OnImageLoadListener {
        void onImageLoad();
    }

    public void setOnImageLoadListener(OnImageLoadListener listener) {
        this.onImageLoadListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_quiz_question, container, false);
        questionTextView = view.findViewById(R.id.questionTextView);
        optionA = view.findViewById(R.id.optionA);
        optionB = view.findViewById(R.id.optionB);
        optionC = view.findViewById(R.id.optionC);
        questionLoadingProgressBar=view.findViewById(R.id.questionLoadingProgressBar);
        imageView=view.findViewById(R.id.imageviewques);
        optionD = view.findViewById(R.id.optionD);


        if (getArguments() != null) {
            questionTextView.setText(getArguments().getString("question"));
            String questionText=questionTextView.getText().toString();
            optionA.setText(getArguments().getString("optionA"));
            optionB.setText(getArguments().getString("optionB"));
            optionC.setText(getArguments().getString("optionC"));
            optionD.setText(getArguments().getString("optionD"));
            correctAnswer = getArguments().getString("correctAnswer");
            int questionIndex = getArguments().getInt("questionIndex");
            loadQuestionWithProgress(questionIndex);


        }




        optionA.setOnClickListener(v -> checkAnswer(optionA.getText().toString()));
        optionB.setOnClickListener(v -> checkAnswer(optionB.getText().toString()));
        optionC.setOnClickListener(v -> checkAnswer(optionC.getText().toString()));
        optionD.setOnClickListener(v -> checkAnswer(optionD.getText().toString()));

        return view;
    }

    private void loadRandomImage(int questionIndex, Runnable onImageLoaded) {
        String[] techImageUrls = {
                "https://picsum.photos/id/0/600/400",
                "https://picsum.photos/id/1/600/400",
                "https://picsum.photos/id/2/600/400",
                "https://picsum.photos/id/3/600/400",
                "https://picsum.photos/id/4/600/400",
                "https://picsum.photos/id/5/600/400",
                "https://picsum.photos/id/6/600/400",
                "https://picsum.photos/id/8/600/400",
                "https://picsum.photos/id/60/600/400",
                "https://picsum.photos/id/180/600/400"
        };

        String query = techImageUrls[questionIndex % techImageUrls.length];

        Glide.with(this)
                .load(query)
                .placeholder(R.drawable.programming)
                .error(R.drawable.technology)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        onImageLoaded.run(); // Proceed even if image fails to load
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        onImageLoaded.run(); // Notify that image load is complete
                        return false;
                    }
                })
                .into(imageView);
    }
    private void loadQuestionWithProgress(int questionIndex) {
        // Show progress bar while loading the question
        questionLoadingProgressBar.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.INVISIBLE);
        optionA.setVisibility(View.INVISIBLE);
        optionB.setVisibility(View.INVISIBLE);
        optionC.setVisibility(View.INVISIBLE);
        optionD.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);

        // Load random image
        loadRandomImage(questionIndex, () -> {
            // Once image is loaded, show all question-related elements
            questionLoadingProgressBar.setVisibility(View.GONE);
            questionTextView.setVisibility(View.VISIBLE);
            optionA.setVisibility(View.VISIBLE);
            optionB.setVisibility(View.VISIBLE);
            optionC.setVisibility(View.VISIBLE);
            optionD.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            // Notify parent activity that the image has finished loading
            if (onImageLoadListener != null) {
                onImageLoadListener.onImageLoad();
            }
        });
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
            shakeScreen();
            showSnackbar(isCorrect);
            ((QuizActivity) getActivity()).loadNextQuestion();
        }

    }
    private void shakeScreen() {
        if (getActivity() != null) {
            View rootView = getActivity().findViewById(android.R.id.content);
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            rootView.startAnimation(shake);
        }
    }
    private void showSnackbar(boolean isCorrect) {
        String message = isCorrect
                ? "Correct Answer!"
                : "Wrong! ";

        int color = isCorrect ? android.graphics.Color.parseColor("#4CAF50") : android.graphics.Color.parseColor("#F44336"); // Green and Red

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();


        snackbarView.setBackgroundColor(color);


        snackbar.show();
    }

}