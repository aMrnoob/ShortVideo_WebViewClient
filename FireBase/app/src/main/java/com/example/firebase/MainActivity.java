package com.example.firebase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideoFireBaseAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.vpager);
        getVideos();

    }

    private void getVideos() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("videos");
        FirebaseRecyclerOptions<Video1Model> options = new FirebaseRecyclerOptions.Builder<Video1Model>()
                .setQuery(mDataBase, Video1Model.class).build();
        videoAdapter = new VideoFireBaseAdapter(options, this);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videoAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        videoAdapter.notifyDataSetChanged();
    }
}