package com.example.firebase;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoFireBaseAdapter extends FirebaseRecyclerAdapter<Video1Model, VideoFireBaseAdapter.MyHolder> {
    private boolean isFav = false;
    private final LifecycleOwner lifecycleOwner;

    public VideoFireBaseAdapter(@NonNull FirebaseRecyclerOptions<Video1Model> options, LifecycleOwner lifecycleOwner) {
        super(options);
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Video1Model model) {
        holder.textVideoTitle.setText(model.getTitle());
        holder.textViewDescription.setText(model.getDesc());
        String videoId = extractYoutubeVideoId(model.getUrl());

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

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView youTubePlayerView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textViewDescription;
        private ImageView imPerson, favorites, imShare, imMore;

        public MyHolder(@NonNull View itemView) {
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
