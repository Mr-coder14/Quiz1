package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {
    private ImageButton imageButtonl;
    private TextView aboutUsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        imageButtonl = findViewById(R.id.back_btn_about_us);
        aboutUsContent = findViewById(R.id.about_us_content);

        imageButtonl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set About Us content
        aboutUsContent.setText(
                "Welcome to QuizFest!\n\n" +
                        "QuizFest is your ultimate destination for fun, interactive, and knowledge-packed quizzes. Whether you're a trivia enthusiast, a curious learner, or someone looking to challenge friends, QuizFest is designed to make your learning journey exciting and rewarding.\n\n" +
                        "At QuizFest, we believe learning should never feel like a chore. That's why we've combined education with entertainment to bring you an app that sharpens your mind, tests your skills, and rewards you along the way!\n\n" +
                        "What Makes QuizFest Special?\n\n" +
                        "1. Wide Range of Quizzes\n" +
                        "   Explore a diverse library of quizzes across various topics, including science, history, entertainment, sports, and more. There’s something for everyone! Whether you’re looking to master new knowledge or simply have fun, QuizFest has got you covered.\n\n" +
                        "2. Earn and Use Coins\n" +
                        "   Complete quizzes and earn coins as rewards. While these coins are purely for in-app purposes and not real currency, they play a vital role in enhancing your QuizFest experience. Use coins to climb the leaderboard and showcase your expertise. Compete with other users and see where you stand among the best!\n\n" +
                        "3. Safe & Secure Communication\n" +
                        "   Connect with like-minded users through our secure communication platform. Share tips, discuss quizzes, and celebrate your victories while enjoying complete privacy. If you encounter inappropriate behavior, you can easily block or report users to ensure a safe and friendly environment.\n\n" +
                        "4. Report Quizzes\n" +
                        "   We are committed to providing high-quality, accurate, and fair quizzes. If you come across any questionable content, our reporting system allows you to flag it for review. Your feedback helps us improve QuizFest for everyone!\n\n" +
                        "5. User-Friendly Experience\n" +
                        "   Our intuitive and sleek design makes it easy to navigate through the app. Whether you're a first-time user or a QuizFest veteran, you’ll find it effortless to explore all the features and content.\n\n" +
                        "Why Choose QuizFest?\n\n" +
                        "- Engagement: From daily challenges to thematic quizzes, there's always something new to explore.\n" +
                        "- Community: Join a growing community of trivia lovers and exchange knowledge.\n" +
                        "- Recognition: Rise to the top of the leaderboard and become a QuizFest champion.\n" +
                        "- Fun & Learning: QuizFest combines the thrill of games with the satisfaction of gaining knowledge.\n\n" +
                        "Our Mission\n\n" +
                        "At QuizFest, our mission is to create a platform where learning meets fun. We aim to foster curiosity, competition, and camaraderie among users from all walks of life. By integrating advanced security features, user feedback mechanisms, and rewarding gameplay, we ensure a safe and enjoyable experience for everyone.\n\n" +
                        "Join QuizFest Today!\n\n" +
                        "Download QuizFest and embark on an exciting journey of learning and fun. Test your knowledge, connect with others, and climb the leaderboard. We’re thrilled to have you on board!\n\n" +
                        "Let’s make learning an adventure—one quiz at a time."
        );
    }
}
