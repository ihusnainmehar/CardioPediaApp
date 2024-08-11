package com.example.cardiopediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DeleteProfileActivity extends AppCompatActivity {
    private Button buttonDeleteProfile;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;
    private DatabaseReference referenceProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        buttonDeleteProfile = findViewById(R.id.button_delete_profile);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonDeleteProfile.setOnClickListener(v -> {
            if (firebaseUser != null) {
                progressBar.setVisibility(View.VISIBLE);
                deleteUserProfile(firebaseUser);
            } else {
                Toast.makeText(DeleteProfileActivity.this, "User details are not available at the moment", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(userID);

        // Delete user profile data from Firebase Database
        referenceProfile.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Delete user authentication data from Firebase Auth
                firebaseUser.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(DeleteProfileActivity.this, "Profile deleted successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DeleteProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(DeleteProfileActivity.this, "Profile deletion failed", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                Toast.makeText(DeleteProfileActivity.this, "Profile deletion failed", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
