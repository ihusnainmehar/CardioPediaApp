package com.example.cardiopediaapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class patientmoreActivity extends AppCompatActivity {

    private TextView DprofileTxtView, SettingsTxtView, RateTextView, ComplainTextView, InviteTextView,AboutTxtView, ContacttxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patientmore);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DprofileTxtView = findViewById(R.id.DProfileTxtView);
        DprofileTxtView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(patientmoreActivity.this, PatProfileActivity.class);
                startActivity(k);
            }
        });
        SettingsTxtView = findViewById(R.id.SettingsTxtView);
        SettingsTxtView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(patientmoreActivity.this, SettingsActivity.class);
                startActivity(k);
            }
        });
        ComplainTextView = findViewById(R.id.ComplainTxtView);
        ComplainTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(patientmoreActivity.this, SubmitComplainActivity.class);
                startActivity(k);
            }
        });
        RateTextView = findViewById(R.id.RateApp);
        RateTextView.setOnClickListener(v -> {
            // Open the app's page on Google Play Store for rating
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(rateIntent);
            } catch (android.content.ActivityNotFoundException e) {
                // If the Google Play Store app is not installed, open the web browser
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(rateIntent);
            }
        });
        InviteTextView=findViewById(R.id.InviteTxtView);
        InviteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inviteIntent = new Intent(Intent.ACTION_SEND);
                inviteIntent.setType("text/plain");
                inviteIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I'd like to invite you to try out this awesome app. Download it from [App Store Link].");

                // Start an activity to show the sharing options
                startActivity(Intent.createChooser(inviteIntent, "Invite a Friend"));

            }
        });

        findViewById(R.id.LogoutTxtView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        AboutTxtView = findViewById(R.id.aboutTxtView);
        AboutTxtView.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage(R.string.about_us)
                    .setPositiveButton("ok", null).show();
        });
        findViewById(R.id.contactusTxtView).setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Contact Us")
                    .setMessage("shehzadahusnain381@gmail.com")
                    .setPositiveButton("ok", null).show();
        });
    }
}