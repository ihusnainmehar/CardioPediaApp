    package com.example.cardiopediaapp;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.squareup.picasso.Picasso;

    public class PatProfileActivity extends AppCompatActivity {
        private static final int UPLOAD_REQUEST_CODE = 1;
        private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile, textViewAddress;
        private ProgressBar progressBar;
        private ImageView imageView;
        private FirebaseAuth authProfile;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pat_profile);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Profile");
            }

            textViewWelcome = findViewById(R.id.textView_show_welcome);
            textViewFullName = findViewById(R.id.textView_show_full_name);
            textViewEmail = findViewById(R.id.textView_show_email);
            textViewDoB = findViewById(R.id.textView_show_dob);
            textViewGender = findViewById(R.id.textView_show_gender);
            textViewMobile = findViewById(R.id.textView_show_mobile);
            textViewAddress = findViewById(R.id.textView_show_address);
            imageView = findViewById(R.id.imageView_profile_dp);
            progressBar = findViewById(R.id.progressBar);

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(PatProfileActivity.this, UploadProfilePicActivity.class);
                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
            });

            authProfile = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = authProfile.getCurrentUser();

            if (firebaseUser == null) {
                Toast.makeText(PatProfileActivity.this, "Something went wrong, User details are not available at the moment", Toast.LENGTH_LONG).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                showUserProfile(firebaseUser);
            }
        }

        private void showUserProfile(FirebaseUser firebaseUser) {
            String userID = firebaseUser.getUid();

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
            referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String gender = dataSnapshot.child("info").child("gender").getValue(String.class);
                        String address = dataSnapshot.child("info").child("address").getValue(String.class);
                        String name = dataSnapshot.child("info").child("name").getValue(String.class);
                        String age = dataSnapshot.child("info").child("age").getValue(String.class);
                        String phone = dataSnapshot.child("info").child("phone").getValue(String.class);

                        textViewWelcome.setText("Welcome! " + name + "!");
                        textViewEmail.setText(email != null ? email : "N/A");
                        textViewGender.setText(gender != null ? gender : "N/A");
                        textViewAddress.setText(address != null ? address : "N/A");
                        textViewFullName.setText(name != null ? name : "N/A");
                        textViewDoB.setText(age != null ? age : "N/A");
                        textViewMobile.setText(phone != null ? phone : "N/A");

                        // Set User Dp (After User has uploaded Profile Pic)
                        String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                        if (photoUrl != null) {
                            Picasso.get().load(photoUrl).into(imageView);
                        } else if (firebaseUser.getPhotoUrl() != null) {
                            Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
                        }
                    } else {
                        Toast.makeText(PatProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PatProfileActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.common_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.menu_refresh) {
                // Refresh Activity
                Intent intent = getIntent();
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            } else if (id == R.id.menu_update_profile) {
                // Update Profile
                Intent intent = new Intent(PatProfileActivity.this, PatProfileUpdateActivity.class);
                startActivity(intent);
            } else if (id == R.id.menu_settings) {
                // Settings
                Intent intent = new Intent(PatProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (id == R.id.menu_change_password) {
                // Change Password
                Intent intent = new Intent(PatProfileActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            } else if (id == R.id.menu_delete_profile) {
                // Delete Profile
                Intent intent = new Intent(PatProfileActivity.this, DeleteProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.menu_logout) {
                // Logout
                authProfile.signOut();
                Intent intent = new Intent(PatProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PatProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri profileImageUri = data.getData();
                    Picasso.get().load(profileImageUri).into(imageView);

                    // You should save this URL to Firebase Database as well
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser != null) {
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        referenceProfile.child("photoUrl").setValue(profileImageUri.toString());
                    }
                }
            }
        }
    }
