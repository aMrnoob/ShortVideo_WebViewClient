package com.example.firebaseretrofit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private final LifecycleOwner lifecycleOwner;
    private Context context;
    private List<VideoModel> videoList;
    private boolean isFav = false;

    public VideoAdapter(Context context, List<VideoModel> videoList, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.videoList = videoList;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.MyViewHolder holder, int position) {
        VideoModel videoModel = videoList.get(position);
        holder.textVideoTitle.setText(videoModel.getTitle());
        holder.textViewDescription.setText(videoModel.getDescription());
        String videoId = extractYoutubeVideoId(videoModel.getUrl());

        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                holder.videoProgressBar.setVisibility(View.GONE);
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFav) {
                    holder.favorites.setImageResource(R.drawable.ic_fill_favorite);
                    isFav = true;
                } else {
                    holder.favorites.setImageResource(R.drawable.ic_favorite);
                    isFav = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView youTubePlayerView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textViewDescription;
        private ImageView imPerson, favorites, imShare, imMore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
            videoProgressBar = itemView.findViewById(R.id.videoProgressBar);
            textVideoTitle = itemView.findViewById(R.id.textVideoTitle);
            textViewDescription = itemView.findViewById(R.id.textVideoDescription);
            imPerson = itemView.findViewById(R.id.imPerson);
            favorites = itemView.findViewById(R.id.favorites);
            imShare = itemView.findViewById(R.id.imShare);
            imMore = itemView.findViewById(R.id.imMore);
            lifecycleOwner.getLifecycle().addObserver(youTubePlayerView);
        }
    }

    private String extractYoutubeVideoId(String url) {
        if (url.contains("shorts/")) {
            return url.substring(url.lastIndexOf("shorts/") + 7);
        } else if (url.contains("watch?v=")) {
            return url.substring(url.lastIndexOf("watch?v=") + 8);
        }
        return url;
    }
}
