package com.example.cardiopediaapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientHistoryActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private Button btnUpload;
    private RecyclerView recyclerViewInvestigations;
    private InvestigationAdapter investigationAdapter;
    private List<Investigation> investigationList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        btnUpload = findViewById(R.id.btnUpload);
        recyclerViewInvestigations = findViewById(R.id.recyclerViewInvestigations);

        recyclerViewInvestigations.setLayoutManager(new LinearLayoutManager(this));
        investigationList = new ArrayList<>();
        investigationAdapter = new InvestigationAdapter(this, investigationList, new InvestigationAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Investigation investigation) {
                deleteInvestigation(investigation);
            }

            @Override
            public void onDownloadClick(Investigation investigation) {
                downloadInvestigation(investigation);
            }
        });
        recyclerViewInvestigations.setAdapter(investigationAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("investigations").child(mAuth.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference().child("investigations").child(mAuth.getUid());

        btnUpload.setOnClickListener(v -> openFilePicker());

        loadInvestigations();
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Investigation File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            showInvestigationNameDialog(fileUri);
        }
    }

    private void showInvestigationNameDialog(Uri fileUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Investigation Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String investigationName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(investigationName)) {
                uploadFile(fileUri, investigationName);
            } else {
                Toast.makeText(PatientHistoryActivity.this, "Investigation name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void uploadFile(Uri fileUri, String investigationName) {
        if (fileUri != null) {
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(fileUri));
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String uploadId = mDatabase.push().getKey();
                        Investigation investigation = new Investigation(uploadId, investigationName, uri.toString(), "image");
                        mDatabase.child(uploadId).setValue(investigation);
                        Toast.makeText(PatientHistoryActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(PatientHistoryActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void loadInvestigations() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                investigationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Investigation investigation = snapshot.getValue(Investigation.class);
                    investigationList.add(investigation);
                }
                investigationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientHistoryActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteInvestigation(Investigation investigation) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(investigation.url);
        fileReference.delete().addOnSuccessListener(aVoid -> mDatabase.child(investigation.id).removeValue()
                .addOnSuccessListener(aVoid1 -> Toast.makeText(PatientHistoryActivity.this, "Investigation deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PatientHistoryActivity.this, "Failed to delete investigation: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void downloadInvestigation(Investigation investigation) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(investigation.url);

        // Create a local file to store the download
        File localFile;
        try {
            localFile = File.createTempFile("investigation", ".jpg");
            fileReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File downloaded successfully
                        Toast.makeText(PatientHistoryActivity.this, "Downloaded to: " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                        // Open the file using a file intent
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", localFile);
                        intent.setDataAndType(fileUri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Toast.makeText(PatientHistoryActivity.this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            Toast.makeText(PatientHistoryActivity.this, "Failed to create temp file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
