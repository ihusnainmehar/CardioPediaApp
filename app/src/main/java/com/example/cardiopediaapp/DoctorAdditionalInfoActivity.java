package com.example.cardiopediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DoctorAdditionalInfoActivity extends AppCompatActivity {

    private static final int PICK_DEGREE_REQUEST = 1;
    private static final int PICK_LICENSE_REQUEST = 2;

    private EditText etQualification, etName, etSpecialization, etHospital, etAddress, etPhoneNumber, etExperience;
    private Button btnSave;
    private ImageButton uploadDegreeButton, uploadLicenseButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private Uri degreeUri, licenseUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_additional_info);

        etQualification = findViewById(R.id.etQualification);
        etName = findViewById(R.id.etName);
        etHospital = findViewById(R.id.etHospital);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etExperience = findViewById(R.id.etExperience);
        etSpecialization = findViewById(R.id.etSpecialization);
        btnSave = findViewById(R.id.btnSave);
        uploadDegreeButton = findViewById(R.id.uploadDegreeButton);
        uploadLicenseButton = findViewById(R.id.uploadLicenseButton);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        uploadDegreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_DEGREE_REQUEST);
            }
        });

        uploadLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_LICENSE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdditionalInfo();
            }
        });
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_DEGREE_REQUEST) {
                degreeUri = data.getData();
                Toast.makeText(this, "Degree file selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_LICENSE_REQUEST) {
                licenseUri = data.getData();
                Toast.makeText(this, "License file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAdditionalInfo() {
        String qualification = etQualification.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();

        if (TextUtils.isEmpty(qualification) || TextUtils.isEmpty(specialization) || TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(hospital) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(experience) || degreeUri == null || licenseUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload both files", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userId = user.getUid();
        final StorageReference degreeRef = mStorage.child("degrees").child(userId + ".pdf");
        final StorageReference licenseRef = mStorage.child("licenses").child(userId + ".pdf");

        degreeRef.putFile(degreeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                degreeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri degreeDownloadUri) {
                        licenseRef.putFile(licenseUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                licenseRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri licenseDownloadUri) {
                                        DoctorInfo doctorInfo = new DoctorInfo(name, qualification, specialization, hospital, address, phoneNumber, experience, degreeDownloadUri.toString(), licenseDownloadUri.toString());

                                        mDatabase.child("users").child(userId).child("info").setValue(doctorInfo)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Redirect to Doctor Profile
                                                            startActivity(new Intent(DoctorAdditionalInfoActivity.this, DoctorHome.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(DoctorAdditionalInfoActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public static class DoctorInfo {
        public String name;
        public String qualification;
        public String specialization;
        public String hospital;
        public String address;
        public String phoneNumber;
        public String experience;
        public String degreeUrl;
        public String licenseUrl;

        public DoctorInfo(String name, String qualification, String specialization, String hospital, String address, String phoneNumber, String experience, String degreeUrl, String licenseUrl) {
            this.name = name;
            this.qualification = qualification;
            this.specialization = specialization;
            this.hospital = hospital;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.experience = experience;
            this.degreeUrl = degreeUrl;
            this.licenseUrl = licenseUrl;
        }
    }
}
