package com.example.cardiopediaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CardioCareActivity extends AppCompatActivity {

    private EditText etQuery;
    private TextView tvResponse;
    private Button btnSend;
    private static final String TAG = "CardioCareActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio_care);

        etQuery = findViewById(R.id.et_query);
        tvResponse = findViewById(R.id.tv_response);
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etQuery.getText().toString().trim();
                if (!query.isEmpty()) {
                    new FetchResponseTask().execute(query);
                }
            }
        });
    }

    private class FetchResponseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... queries) {
            String query = queries[0];
            String response = "";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer AIzaSyA4h0a2JkF6u7eNGr4bbc2Yl-K7pGXz83o");
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject requestBody = new JSONObject();
                requestBody.put("prompt", "You are a cardiologist who has a lot of experience in diagnosing and treating heart patients. Your job is to guide the user about post-heart surgery exercises, care and preventions, and other heart-related queries.\nHuman: Hello\nAI: Hello, I am your virtual AI doctor. How can I help you?\nHuman: " + query + "\nAI:");
                requestBody.put("max_tokens", 150);

                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == 200) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    response = sb.toString();
                } else {
                    Log.e(TAG, "Error: Response code " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching response", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String answer = responseJson.getJSONArray("choices").getJSONObject(0).getString("text");
                    tvResponse.setText(answer.trim());
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing response", e);
                    tvResponse.setText("An error occurred while parsing the response.");
                }
            } else {
                tvResponse.setText("An error occurred while fetching the response.");
            }
        }
    }
}
