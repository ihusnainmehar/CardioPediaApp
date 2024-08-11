package com.example.cardiopediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePicActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button buttonUpload, buttonSelect;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        imageView = findViewById(R.id.imageView_profile_dp);
        buttonSelect = findViewById(R.id.upload_pic_choose_button);
        buttonUpload = findViewById(R.id.upload_pic_button);

        progressDialog = new ProgressDialog(this);

        buttonSelect.setOnClickListener(v -> openFileChooser());
        buttonUpload.setOnClickListener(v -> uploadImage());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                        .child(firebaseUser.getUid() + ".jpg");

                storageReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                            referenceProfile.child("photoUrl").setValue(uri.toString()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UploadProfilePicActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    Intent resultIntent = new Intent();
                                    resultIntent.setData(uri);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    Toast.makeText(UploadProfilePicActivity.this, "Failed to upload image URL", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            });
                        })).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(UploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
