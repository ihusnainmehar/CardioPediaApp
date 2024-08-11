package com.example.cardiopediaapp;

public class Alarm {
    private String id;
    private String medicineName;
    private int frequency;
    private int duration;

    public Alarm() {
        // Default constructor required for calls to DataSnapshot.getValue(Alarm.class)
    }

    public Alarm(String id, String medicineName, int frequency, int duration) {
        this.id = id;
        this.medicineName = medicineName;
        this.frequency = frequency;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

