package com.example.firebaseretrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideoAdapter videosAdapter;
    private List<VideoModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.vpager);
        list = new ArrayList<>();
        getVideos();
    }

    private void getVideos() {
        APIService.serviceapi.getVideos().enqueue(new Callback<MessageVideoModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageVideoModel> call, @NonNull Response<MessageVideoModel> response) {
                assert response.body() != null;
                list = response.body().getResult();
                videosAdapter = new VideoAdapter(getApplicationContext(), list, MainActivity.this);
                viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                viewPager2.setAdapter(videosAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<MessageVideoModel> call, @NonNull Throwable t) {
                Log.d("TAG", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}