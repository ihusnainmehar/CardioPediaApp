package com.example.cardiopediaapp;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
