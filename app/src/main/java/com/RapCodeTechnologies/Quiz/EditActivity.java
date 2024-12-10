package com.RapCodeTechnologies.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Adaptors.ImageGridAdapter;

public class EditActivity extends AppCompatActivity {
    private ImageView imageView,edit;
    private ImageView back;
    private EditText username,bio;
    private String resourceName;
    private LinearLayout discard,confirm,gr,iuo;
    private ProgressBar progressBar;
    private String userid;
    private DatabaseReference databaseReference;
    private int[] imageResources = {
            R.drawable.person3,
            R.drawable.profile,
            R.drawable.unknownprofile,
            R.drawable.edit
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        discard=findViewById(R.id.deiscardbutton);
        confirm=findViewById(R.id.SaveButton);
        back=findViewById(R.id.backpr);
        gr=findViewById(R.id.gr);
        iuo=findViewById(R.id.iuo);
        username=findViewById(R.id.usernameInput);
        imageView=findViewById(R.id.profileImageu);
        edit=findViewById(R.id.chamgepic);
        progressBar=findViewById(R.id.progressBaredit);
        gr.setVisibility(View.GONE);
        iuo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        resourceName="unknownprofile";
        bio=findViewById(R.id.bioInput);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        findViewById(R.id.hintmessage).setVisibility(View.GONE);
        edit.setOnClickListener(view -> showImageSelectionDialog());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSaveUsername();
            }
        });
        loadUserDetails();

    }

    private void loadUserDetails() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
                    String userBio = snapshot.child("bio").getValue(String.class);
                    String profileImageKey = snapshot.child("profile").getValue(String.class);


                    if (userName != null) {
                        username.setText(userName);
                    }
                    if (userBio != null) {
                        bio.setText(userBio);
                    }


                    if (profileImageKey != null) {
                        resourceName = profileImageKey;
                        int imageResId = getResources().getIdentifier(profileImageKey, "drawable", getPackageName());
                        if (imageResId != 0) {
                            imageView.setImageResource(imageResId);
                        } else {
                            imageView.setImageResource(R.drawable.unknownprofile);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.unknownprofile);
                    }
                } else {
                    Toast.makeText(EditActivity.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                }
                gr.setVisibility(View.VISIBLE);
                iuo.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                gr.setVisibility(View.VISIBLE);
                iuo.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_selection, null);

        GridView gridView = dialogView.findViewById(R.id.imageGridView);
        ImageGridAdapter adapter = new ImageGridAdapter(this, imageResources);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            imageView.setImageResource(imageResources[position]);
             resourceName = getResources().getResourceEntryName(imageResources[position]);

        });

        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setTitle("Select Profile Image");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void validateAndSaveUsername() {
        gr.setVisibility(View.VISIBLE);
        iuo.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String enteredUsername = username.getText().toString().trim();
        String bioe=bio.getText().toString();

        if (TextUtils.isEmpty(enteredUsername)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(EditActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bioe)) {
            progressBar.setVisibility(View.GONE);
            bio.setError("Bio cannot be Empty");
            Toast.makeText(EditActivity.this, "Bio cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(userid).child("profile").setValue(resourceName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(EditActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                    }
                });



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean isUsernameUnique = true;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String existingUsername = userSnapshot.child("name").getValue(String.class);
                    String existingUserId = userSnapshot.child("userid").getValue(String.class);

                    if (existingUsername != null && existingUsername.equalsIgnoreCase(enteredUsername)
                            && !existingUserId.equals(userid)) {
                        isUsernameUnique = false;
                        findViewById(R.id.hintmessage).setVisibility(View.VISIBLE);
                        break;
                    }
                }

                if (isUsernameUnique) {
                    findViewById(R.id.hintmessage).setVisibility(View.GONE);

                    databaseReference.child(userid).child("name").setValue(enteredUsername)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    databaseReference.child(userid).child("bio").setValue(bioe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                                Toast.makeText(EditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditActivity.this, "Failed to update Profile", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditActivity.this, "Username is already used", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}