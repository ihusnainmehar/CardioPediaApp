package com.example.cardiopediaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatProfileUpdateActivity extends AppCompatActivity {

    private EditText editTextName, editTextDoB, editTextPhone, editTextAddress;
    private RadioGroup radioGroupGender;
    private Button buttonUpdateProfile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_profile_update);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Profile");
        }

        editTextName = findViewById(R.id.editText_update_name);
        editTextDoB = findViewById(R.id.editText_update_dob);
        radioGroupGender = findViewById(R.id.radioGroup_gender);
        editTextPhone = findViewById(R.id.editText_update_phone);
        editTextAddress = findViewById(R.id.editText_update_address);
        buttonUpdateProfile = findViewById(R.id.button_update_profile);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonUpdateProfile.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String dob = editTextDoB.getText().toString().trim();
            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                editTextName.setError("Name is required");
                editTextName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(dob)) {
                editTextDoB.setError("Date of Birth is required");
                editTextDoB.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(gender)) {
                Toast.makeText(PatProfileUpdateActivity.this, "Gender is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("Phone is required");
                editTextPhone.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(address)) {
                editTextAddress.setError("Address is required");
                editTextAddress.requestFocus();
                return;
            }

            if (firebaseUser != null) {
                progressBar.setVisibility(View.VISIBLE);
                updateProfile(firebaseUser, name, dob, gender, phone, address);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser, String name, String dob, String gender, String phone, String address) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(userID);

        referenceProfile.child("info").child("name").setValue(name);
        referenceProfile.child("info").child("dob").setValue(dob);
        referenceProfile.child("info").child("gender").setValue(gender);
        referenceProfile.child("info").child("phone").setValue(phone);
        referenceProfile.child("info").child("address").setValue(address).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatProfileUpdateActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(PatProfileUpdateActivity.this, "Update Failed", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}
