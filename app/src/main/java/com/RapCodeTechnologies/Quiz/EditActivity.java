package com.RapCodeTechnologies.Quiz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class EditActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageView back;
    private EditText username,bio;
    private LinearLayout discard,confirm;
    private String userid;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        discard=findViewById(R.id.deiscardbutton);
        confirm=findViewById(R.id.SaveButton);
        back=findViewById(R.id.backpr);
        username=findViewById(R.id.usernameInput);
        bio=findViewById(R.id.bioInput);
        userid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        findViewById(R.id.hintmessage).setVisibility(View.GONE);

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

    }
    private void validateAndSaveUsername() {
        String enteredUsername = username.getText().toString().trim();
        String bioe=bio.getText().toString();

        if (TextUtils.isEmpty(enteredUsername)) {
            Toast.makeText(EditActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bioe)) {
            bio.setError("Bio cannot be Empty");
            Toast.makeText(EditActivity.this, "Bio cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    // Save the new username to Firebase
                    databaseReference.child(userid).child("name").setValue(enteredUsername)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    databaseReference.child(userid).child("bio").setValue(bioe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(EditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(EditActivity.this, "Failed to update Profile", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(EditActivity.this, "Username is already used", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EditActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}