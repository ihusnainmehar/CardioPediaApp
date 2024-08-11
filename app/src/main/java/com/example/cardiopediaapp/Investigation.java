package com.example.cardiopediaapp;

public class Investigation {
    public String id;
    public String name;
    public String url;
    public String type;

    public Investigation() {
        // Default constructor required for calls to DataSnapshot.getValue(Investigation.class)
    }

    public Investigation(String id, String name, String url, String type) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.type = type;
    }
}
