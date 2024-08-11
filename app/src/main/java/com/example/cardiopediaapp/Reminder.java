package com.example.cardiopediaapp;

public class Reminder {
    private String id;
    private String medicineName;
    private String dosage;
    private int hour;
    private int minute;
    private String frequency;

    public Reminder() {
        // Default constructor required for calls to DataSnapshot.getValue(Reminder.class)
    }

    public Reminder(String id, String medicineName, String dosage, int hour, int minute, String frequency) {
        this.id = id;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.hour = hour;
        this.minute = minute;
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getFrequency() {
        return frequency;
    }
}

