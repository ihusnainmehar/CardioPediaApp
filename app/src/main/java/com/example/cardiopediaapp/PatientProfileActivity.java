package com.example.cardiopediaapp;

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

public class PatientProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvGender, tvAddress, tvAge, tvDiabetes, tvHypertension, tvPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        tvAddress = findViewById(R.id.tvAddress);
        tvAge = findViewById(R.id.tvAge);
        tvName = findViewById(R.id.tvName);
        tvDiabetes = findViewById(R.id.tvDiabetes);
        tvHypertension = findViewById(R.id.tvHypertension);
        tvPhone = findViewById(R.id.tvPhoneNumber);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadProfile();
    }

    private void loadProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String gender = dataSnapshot.child("info").child("gender").getValue(String.class);
                        String address = dataSnapshot.child("info").child("address").getValue(String.class);
                        String name = dataSnapshot.child("info").child("name").getValue(String.class);
                        String age = dataSnapshot.child("info").child("age").getValue(String.class);
                        String diabetes = dataSnapshot.child("info").child("diabetes").getValue(String.class);
                        String hypertension = dataSnapshot.child("info").child("hypertension").getValue(String.class);
                        String phone = dataSnapshot.child("info").child("phone").getValue(String.class);

                        tvEmail.setText(email != null ? email : "N/A");
                        tvGender.setText(gender != null ? gender : "N/A");
                        tvAddress.setText(address != null ? address : "N/A");
                        tvName.setText(name != null ? name : "N/A");
                        tvAge.setText(age != null ? age : "N/A");
                        tvDiabetes.setText(diabetes != null ? diabetes : "N/A");
                        tvHypertension.setText(hypertension != null ? hypertension : "N/A");
                        tvPhone.setText(phone != null ? phone : "N/A");
                    } else {
                        Toast.makeText(PatientProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PatientProfileActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }
}
