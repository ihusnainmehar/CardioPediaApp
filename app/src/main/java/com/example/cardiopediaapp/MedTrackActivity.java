package com.example.cardiopediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedTrackActivity extends AppCompatActivity {

    private ListView reminderListView;
    private Button addReminderButton;
    private DatabaseReference databaseReminders;
    private ArrayList<Reminder> reminderList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> reminderNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_track);

        reminderListView = findViewById(R.id.reminderListView);
        addReminderButton = findViewById(R.id.addReminderButton);

        reminderList = new ArrayList<>();
        reminderNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderNames);
        reminderListView.setAdapter(adapter);

        databaseReminders = FirebaseDatabase.getInstance().getReference("reminders");

        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MedTrackActivity.this, AddReminderActivity.class));
            }
        });

        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Reminder reminder = reminderList.get(position);
                Intent intent = new Intent(MedTrackActivity.this, EditReminderActivity.class);
                intent.putExtra("reminderId", reminder.getId());
                startActivity(intent);
            }
        });

        reminderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = reminderList.get(position);
                deleteReminder(reminder);
                return true;
            }
        });

        loadReminders();
    }

    private void loadReminders() {
        databaseReminders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reminderList.clear();
                reminderNames.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Reminder reminder = postSnapshot.getValue(Reminder.class);
                    reminderList.add(reminder);
                    reminderNames.add(reminder.getMedicineName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MedTrackActivity.this, "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReminder(Reminder reminder) {
        databaseReminders.child(reminder.getId()).removeValue();
        NotificationUtils.cancelNotification(this, reminder);
        reminderList.remove(reminder);
        reminderNames.remove(reminder.getMedicineName());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
    }

}
