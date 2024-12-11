package Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RapCodeTechnologies.Quiz.QuizLevelActivity;
import com.RapCodeTechnologies.Quiz.R;

import java.util.ArrayList;

public class RecentQuizesAdaptor extends RecyclerView.Adapter<RecentQuizesAdaptor.viewHolder> {
    private ArrayList<String> recentquizzes;
    private Context context;

    public RecentQuizesAdaptor(ArrayList<String> arrayList,Context context){
        this.recentquizzes=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public RecentQuizesAdaptor.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recentquizes_item,parent,false);
        return new RecentQuizesAdaptor.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentQuizesAdaptor.viewHolder holder, int position) {
        String name=recentquizzes.get(position);
        holder.bind(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, QuizLevelActivity.class);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recentquizzes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView categoryImage;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.recentText);
            categoryImage=itemView.findViewById(R.id.recentImage);
        }

        public void bind(String categoryName) {
            textView.setText(categoryName);
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
