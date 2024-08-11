package com.example.cardiopediaapp;

public class MedicineReminder {
    public String medicineName;
    public String frequency;

    public MedicineReminder() {
        // Default constructor required for calls to DataSnapshot.getValue(MedicineReminder.class)
    }

    public MedicineReminder(String medicineName, String frequency) {
        this.medicineName = medicineName;
        this.frequency = frequency;
    }
}

