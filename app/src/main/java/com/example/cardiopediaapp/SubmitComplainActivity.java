package com.example.cardiopediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SubmitComplainActivity extends AppCompatActivity {

    private EditText complaintEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_complain);

        complaintEditText = findViewById(R.id.complaintEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String complaintText = complaintEditText.getText().toString();
            if (complaintText.isEmpty()) {
                Toast.makeText(this, "Complain text required", Toast.LENGTH_SHORT).show();
                return;
            }

            submitComplaint(complaintText);
        });
    }

    private void submitComplaint(String complaintText) {
        composeEmail(new String[]{"shehzadahusnain381@gmail.com"}, complaintText);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Complain");

        startActivity(Intent.createChooser(intent, "Email via..."));
    }
}
