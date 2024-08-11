package com.example.cardiopediaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddReminderActivity extends AppCompatActivity {

    private EditText medicineName, dosage, frequency;
    private TimePicker timePicker;
    private Button saveReminder;
    private DatabaseReference databaseReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        medicineName = findViewById(R.id.medicineName);
        dosage = findViewById(R.id.dosage);
        timePicker = findViewById(R.id.timePicker);
        frequency = findViewById(R.id.frequency);
        saveReminder = findViewById(R.id.saveReminder);

        // Initialize Firebase Database
        databaseReminders = FirebaseDatabase.getInstance().getReference("reminders");

        saveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });
    }

    private void addReminder() {
        String name = medicineName.getText().toString().trim();
        String dose = dosage.getText().toString().trim();
        String freq = frequency.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dose) || TextUtils.isEmpty(freq)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        String id = databaseReminders.push().getKey();
        Reminder reminder = new Reminder(id, name, dose, hour, minute, freq);

        if (id != null) {
            databaseReminders.child(id).setValue(reminder);
            NotificationUtils.scheduleNotification(this, reminder);
            Toast.makeText(this, "Reminder added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding reminder", Toast.LENGTH_SHORT).show();
        }
    }
}
