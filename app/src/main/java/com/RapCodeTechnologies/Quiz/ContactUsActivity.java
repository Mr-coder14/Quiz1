package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView contactUsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        backBtn = findViewById(R.id.back);
        contactUsContent = findViewById(R.id.contact_us_content);

        // Set the content for the Contact Us page
        contactUsContent.setText(
                "We’d love to hear from you!\n\n" +
                        "At QuizFest, we value our users and are always here to assist you. Whether you have questions, suggestions, or feedback about the app, feel free to get in touch with us.\n\n" +
                        "📧 Email Us\n" +
                        "For any inquiries or requests, please send us an email at:\n" +
                        "rapcodetechnologies@gmail.com\n\n" +
                        "We strive to respond to all queries within 24-48 hours.\n\n" +
                        "💡 How We Can Help:\n" +
                        "• Got suggestions for new quiz topics? Let us know!\n" +
                        "• Encountering technical issues? Reach out, and we’ll work on resolving them.\n" +
                        "• Have general questions or feedback? Your input helps us improve QuizFest!\n" +
                        "• Need to delete your account or personal details? Simply send us a request, and we will process it promptly.\n\n" +
                        "Your feedback is invaluable to us as we continue to enhance your QuizFest experience. Thank you for being a part of our community!\n\n" +
                        "Stay curious and keep quizzing! 🎉"
        );

        // Back button functionality
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
