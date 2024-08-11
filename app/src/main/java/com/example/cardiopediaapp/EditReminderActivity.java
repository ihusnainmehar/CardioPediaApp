package com.example.cardiopediaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditReminderActivity extends AppCompatActivity {

    private EditText medicineName, dosage, frequency;
    private TimePicker timePicker;
    private Button saveReminder;
    private DatabaseReference databaseReminders;
    private String reminderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        medicineName = findViewById(R.id.medicineName);
        dosage = findViewById(R.id.dosage);
        timePicker = findViewById(R.id.timePicker);
        frequency = findViewById(R.id.frequency);
        saveReminder = findViewById(R.id.saveReminder);

        reminderId = getIntent().getStringExtra("reminderId");

        // Initialize Firebase Database
        databaseReminders = FirebaseDatabase.getInstance().getReference("reminders");

        // Load the existing reminder details
        loadReminderDetails(reminderId);

        saveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReminder();
            }
        });
    }

    private void loadReminderDetails(String reminderId) {
        databaseReminders.child(reminderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                if (reminder != null) {
                    medicineName.setText(reminder.getMedicineName());
                    dosage.setText(reminder.getDosage());
                    timePicker.setCurrentHour(reminder.getHour());
                    timePicker.setCurrentMinute(reminder.getMinute());
                    frequency.setText(reminder.getFrequency());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditReminderActivity.this, "Failed to load reminder details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReminder() {
        String name = medicineName.getText().toString().trim();
        String dose = dosage.getText().toString().trim();
        String freq = frequency.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dose) || TextUtils.isEmpty(freq)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Reminder reminder = new Reminder(reminderId, name, dose, hour, minute, freq);

        databaseReminders.child(reminderId).setValue(reminder);
        NotificationUtils.scheduleNotification(this, reminder);
        Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
