package Adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.QuizLevelActivity;
import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<String> categories;
    private String currentUserId;

    public CategoryAdapter(Context context, ArrayList<String> categories) {
        this.context = context;
        this.categories = categories;
        this.currentUserId = FirebaseAuth.getInstance().getUid();
    }
    public void updateList(ArrayList<String> newList) {
        this.categories = newList; // Update the adapter's data
        notifyDataSetChanged(); // Notify the adapter about the data change
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categories.get(position);
        holder.bind(categoryName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuizLevelActivity.class);
                intent.putExtra("name", categoryName);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showOptionsDialog(categoryName, holder.categoryImage);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void showOptionsDialog(String categoryName, ImageView categoryImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an Option")
                .setItems(new String[]{"Save Quiz", "Report Quiz"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                saveQuiz(categoryName, categoryImage);
                                break;
                            case 1:
                                reportQuiz(categoryName);
                                break;
                        }
                    }
                })
                .show();
    }

    private void saveQuiz(String categoryName, ImageView categoryImage) {
        // Save quiz data to Firebase under "savedQuizzes/{currentUserId}/{categoryName}"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("savedQuizzes");
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("name", categoryName);
        // Replace with actual image ID or URL

        databaseReference.child(currentUserId).child(categoryName).setValue(saveData)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Quiz saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to save quiz", Toast.LENGTH_SHORT).show());
    }

    private void reportQuiz(String categoryName) {
        // Show a dialog with radio buttons to select a report reason
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View reportView = LayoutInflater.from(context).inflate(R.layout.dialog_report_quiz, null);
        RadioGroup radioGroup = reportView.findViewById(R.id.radioGroup);
        EditText additionalInfo = reportView.findViewById(R.id.additionalInfo);

        builder.setView(reportView)
                .setTitle("Report Quiz")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = reportView.findViewById(selectedId);

                        if (selectedRadioButton != null) {
                            String reportReason = selectedRadioButton.getText().toString();
                            String additionalMessage = additionalInfo.getText().toString().trim();

                            // Save report data to Firebase under "reportedQuizzes/{categoryName}/{currentUserId}"
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reportedQuizzes");
                            Map<String, Object> reportData = new HashMap<>();
                            reportData.put("userId", currentUserId);
                            reportData.put("reason", reportReason);
                            reportData.put("message", additionalMessage);

                            databaseReference.child(categoryName).child(currentUserId).setValue(reportData)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Report submitted successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to submit report", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(context, "Please select a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryText;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryText = itemView.findViewById(R.id.categoryText);
        }

        public void bind(String categoryName) {
            categoryText.setText(categoryName);
            if (categoryName.equals("Android Development")) {
                categoryImage.setImageResource(R.drawable.androoid);
            }
            if (categoryName.equals("Web Development")) {
                categoryImage.setImageResource(R.drawable.webb);
            }
            if (categoryName.equals("Php Programming")) {
                categoryImage.setImageResource(R.drawable.phpp);
            }
            if (categoryName.equals("C Programming")) {
                categoryImage.setImageResource(R.drawable.cprogramming);
            }
            if (categoryName.equals("C++ ")) {
                categoryImage.setImageResource(R.drawable.cpp);
            }
            if (categoryName.equals("CSharp Programming")) {
                categoryImage.setImageResource(R.drawable.csharp);
            }
            if (categoryName.equals("Cloud Computing")) {
                categoryImage.setImageResource(R.drawable.cloudcompute);
            }
            if (categoryName.equals("Swift")) {
                categoryImage.setImageResource(R.drawable.swift);
            }
            if (categoryName.equals("Java")) {
                categoryImage.setImageResource(R.drawable.java);
            }
            if (categoryName.equals("Java Script")) {
                categoryImage.setImageResource(R.drawable.javascript);
            }
            if (categoryName.equals("Python")) {
                categoryImage.setImageResource(R.drawable.python);
            }
        }
    }
}