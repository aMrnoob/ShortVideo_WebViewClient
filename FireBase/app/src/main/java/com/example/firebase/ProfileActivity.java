package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_PICK = 1001;
    private static final int REQUEST_IMAGE_PICK = 1002;
    private de.hdodenhof.circleimageview.CircleImageView ivAvatar;
    private RecyclerView rvVideo;
    private List<Video1Model> videoList = new ArrayList<>();
    private VideoAdapter videoAdapter;

    private String uid;
    private TextView tvFullName, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvLogout = findViewById(R.id.btnLogout);
        loadInfo();

        rvVideo = findViewById(R.id.rvVideo);
        rvVideo.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(videoList);
        rvVideo.setAdapter(videoAdapter);

        loadVideosByUid();

        ivAvatar = findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        ImageView ivComeBack = findViewById(R.id.btnComeBack);
        ivComeBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        TextView btnUpload = findViewById(R.id.tvUploadVideo);
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_VIDEO_PICK);
        });

        tvLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();

            if (requestCode == REQUEST_VIDEO_PICK) {
                uploadVideoToCloudinary(selectedUri);

            } else if (requestCode == REQUEST_IMAGE_PICK) {
                ivAvatar.setImageURI(selectedUri);
                uploadImageToCloudinary(selectedUri);
            }
        }
    }

    private void uploadVideoToCloudinary(Uri videoUri) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "duwhghs06");
        config.put("api_key", "514272192613562");
        config.put("api_secret", "PwrY9du7Eb9wHEdT4OTZrQAwuNA");

        Cloudinary cloudinary = new Cloudinary(config);

        new Thread(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(videoUri)) {
                Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
                        "resource_type", "video",
                        "folder", "c8c265acd4096801b2b7641fa547d0ce3a"
                ));

                String uploadedUrl = (String) uploadResult.get("secure_url");
                Log.d("Cloudinary", "Uploaded video to: " + uploadedUrl);

                runOnUiThread(() -> showVideoInfoDialog(uploadedUrl));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showVideoInfoDialog(String videoUrl) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nhập thông tin video");

        final android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_video_info, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            EditText etTitle = dialogView.findViewById(R.id.etVideoTitle);
            EditText etDesc = dialogView.findViewById(R.id.etVideoDesc);

            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!title.isEmpty() && !desc.isEmpty()) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("videos");
                String videoId = database.push().getKey();
                Video1Model video = new Video1Model(title, desc, videoUrl, uid);
                if (videoId != null) {
                    database.child(videoId).setValue(video);
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


    private void uploadImageToCloudinary(Uri imageUri) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "duwhghs06");
        config.put("api_key", "514272192613562");
        config.put("api_secret", "PwrY9du7Eb9wHEdT4OTZrQAwuNA");

        Cloudinary cloudinary = new Cloudinary(config);

        new Thread(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
                Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
                        "resource_type", "image",
                        "folder", "cb1a79ec1d892a5409a9ffe2f9416cf62f"
                ));

                String uploadedUrl = (String) uploadResult.get("secure_url");
                Log.d("Cloudinary", "Avatar uploaded: " + uploadedUrl);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    userRef.child("avatarUrl").setValue(uploadedUrl);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            userRef.child("fullName").get().addOnSuccessListener(snapshot -> {
                String name = snapshot.getValue(String.class);
                if (name != null) {
                    tvFullName.setText(name);
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                tvFullName.setText("Không thể tải tên");
            });

            userRef.child("avatarUrl").get().addOnSuccessListener(snapshot -> {
                String avatarUrl = snapshot.getValue(String.class);
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivAvatar);
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Log.e("Firebase", "Không thể tải avatar");
            });
        }
    }

    private void loadVideosByUid() {
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
        videosRef.get().addOnSuccessListener(snapshot -> {
            videoList.clear();
            for (DataSnapshot videoSnap : snapshot.getChildren()) {
                Video1Model video = videoSnap.getValue(Video1Model.class);
                if (video != null && uid.equals(video.getUid())) {
                    videoList.add(video);
                }
            }
            videoAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Log.e("Firebase", "Failed to load videos by uid");
        });
    }

}