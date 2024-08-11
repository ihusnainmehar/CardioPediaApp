package com.example.cardiopediaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextFullName, editTextSpecialization, editTextQualification, editTextMobile, editTextAddress;
    private Button buttonUpdateProfile;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;
    private DatabaseReference referenceProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextFullName = findViewById(R.id.editText_full_name);
        editTextSpecialization = findViewById(R.id.editText_specialization);
        editTextQualification = findViewById(R.id.editText_qualification);
        editTextMobile = findViewById(R.id.editText_mobile);
        editTextAddress = findViewById(R.id.editText_address);
        buttonUpdateProfile = findViewById(R.id.button_update_profile);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonUpdateProfile.setOnClickListener(v -> {
            String fullName = editTextFullName.getText().toString().trim();
            String specialization = editTextSpecialization.getText().toString().trim();
            String qualification = editTextQualification.getText().toString().trim();
            String mobile = editTextMobile.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            if (TextUtils.isEmpty(fullName)) {
                editTextFullName.setError("Full Name is required");
                editTextFullName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(specialization)) {
                editTextSpecialization.setError("Specialization is required");
                editTextSpecialization.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(qualification)) {
                editTextQualification.setError("Qualification is required");
                editTextQualification.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(mobile)) {
                editTextMobile.setError("Mobile number is required");
                editTextMobile.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(address)) {
                editTextAddress.setError("Address is required");
                editTextAddress.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            updateProfile(firebaseUser, fullName, specialization, qualification, mobile, address);
        });
    }

    private void updateProfile(FirebaseUser firebaseUser, String fullName, String specialization, String qualification, String mobile, String address) {
        String userID = firebaseUser.getUid();
        referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(userID);

        referenceProfile.child("name").setValue(fullName);
        referenceProfile.child("info").child("specialization").setValue(specialization);
        referenceProfile.child("info").child("qualification").setValue(qualification);
        referenceProfile.child("info").child("phoneNumber").setValue(mobile);
        referenceProfile.child("info").child("address").setValue(address)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Profile update failed", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
