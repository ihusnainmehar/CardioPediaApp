package com.example.cardiopediaapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvQualification, tvSpecialization, tvHospital, tvAddress, tvPhoneNumber, tvExperience;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvHospital = findViewById(R.id.tvHospital);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvExperience = findViewById(R.id.tvExperience);
        tvQualification = findViewById(R.id.tvQualification);
        tvSpecialization = findViewById(R.id.tvSpecialization);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadProfile();
    }

    private void loadProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String qualification = dataSnapshot.child("info").child("qualification").getValue(String.class);
                    String specialization = dataSnapshot.child("info").child("specialization").getValue(String.class);
                    String hospital = dataSnapshot.child("info").child("hospital").getValue(String.class);
                    String address = dataSnapshot.child("info").child("address").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("info").child("phoneNumber").getValue(String.class);
                    String experience = dataSnapshot.child("info").child("experience").getValue(String.class);

                    tvName.setText(name != null ? name : "N/A");
                    tvEmail.setText(email != null ? email : "N/A");
                    tvQualification.setText(qualification != null ? qualification : "N/A");
                    tvSpecialization.setText(specialization != null ? specialization : "N/A");
                    tvHospital.setText(hospital != null ? hospital : "N/A");
                    tvAddress.setText(address != null ? address : "N/A");
                    tvPhoneNumber.setText(phoneNumber != null ? phoneNumber : "N/A");
                    tvExperience.setText(experience != null ? experience : "N/A");
                } else {
                    Toast.makeText(DoctorProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorProfileActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
