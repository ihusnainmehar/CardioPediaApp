package com.example.cardiopediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientAdditionalInfoActivity extends AppCompatActivity {

    private EditText etName, etAddress, etAge, etPhone;
    private RadioGroup radioGender, radioDiabetes, radioHypertension;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_additional_info);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        radioGender = findViewById(R.id.radioGender);
        radioDiabetes = findViewById(R.id.radioDiabetes);
        radioHypertension = findViewById(R.id.radioHypertension);
        btnSave = findViewById(R.id.btnSave);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdditionalInfo();
            }
        });
    }

    private void saveAdditionalInfo() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        int selectedGenderId = radioGender.getCheckedRadioButtonId();
        int selectedDiabetesId = radioDiabetes.getCheckedRadioButtonId();
        int selectedHypertensionId = radioHypertension.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(age) || TextUtils.isEmpty(phone) ||
                selectedGenderId == -1 || selectedDiabetesId == -1 || selectedHypertensionId == -1) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGender = findViewById(selectedGenderId);
        RadioButton selectedDiabetes = findViewById(selectedDiabetesId);
        RadioButton selectedHypertension = findViewById(selectedHypertensionId);

        String gender = selectedGender.getText().toString();
        String diabetes = selectedDiabetes.getText().toString();
        String hypertension = selectedHypertension.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        PatientInfo patientInfo = new PatientInfo(name, gender, address, age, diabetes, hypertension, phone);
        mDatabase.child("users").child(userId).child("info").setValue(patientInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Redirect to Patient Profile
                            startActivity(new Intent(PatientAdditionalInfoActivity.this, PatientHome.class));
                            finish();
                        } else {
                            Toast.makeText(PatientAdditionalInfoActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class PatientInfo {
        public String name;
        public String gender;
        public String address;
        public String age;
        public String diabetes;
        public String hypertension;
        public String phone;

        public PatientInfo(String name, String gender, String address, String age, String diabetes, String hypertension, String phone) {
            this.name = name;
            this.gender = gender;
            this.address = address;
            this.age = age;
            this.diabetes = diabetes;
            this.hypertension = hypertension;
            this.phone = phone;
        }
    }
}
