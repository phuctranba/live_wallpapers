package com.example.photo_wallpapers.PreviewVideoActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_wallpapers.Data.DatabaseHelper;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.DownloadFile;
import com.example.photo_wallpapers.Util.OnTaskCompleted;
import com.example.photo_wallpapers.Util.Utilities;
import com.example.photo_wallpapers.Util.VideoLiveWallpaper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class PreviewVideoAdapter extends RecyclerView.Adapter<PreviewVideoAdapter.ViewHolder> implements OnTaskCompleted {

    private List<Wallpaper> data;
    private Context context;
    private boolean isStorage;
    private boolean setWallpaper = false;
    private boolean isShare = false;
    private DatabaseHelper databaseHelper;

    public PreviewVideoAdapter(Context context, List<Wallpaper> data, boolean isStorage) {
        this.context = context;
        this.data = data;
        this.isStorage = isStorage;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PreviewVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView =
                inflater.inflate(R.layout.item_video_preview, parent, false);

        return new PreviewVideoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewVideoAdapter.ViewHolder holder, int position) {
        Wallpaper wallpaper = data.get(position);

        holder.setDataVideo(wallpaper);

        holder.imageLike.setVisibility(isStorage ? View.GONE : View.VISIBLE);
        holder.imageDownload.setVisibility(isStorage ? View.GONE : (new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).exists() ? View.GONE : View.VISIBLE));
        Picasso.get().load(wallpaper.isLike() ? R.drawable.ic_live_true : R.drawable.ic_live_false).into(holder.imageLike);

        holder.imageViewSetAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setWallpaper = true;
                isShare = false;
                if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_VIDEO)) {
                    Utilities.copyFile(new File(wallpaper.getUri()), new File(context.getFilesDir() + "/file_wallpapers.mp4"));
                    setWallpaper();
                } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()), new File(context.getFilesDir() + "/file_wallpapers.mp4"));
                    setWallpaper();
                } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()), new File(context.getFilesDir() + "/file_wallpapers.mp4"));
                    setWallpaper();
                } else
                    downloadFile(wallpaper);
            }
        });

        holder.imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper = false;
                isShare = false;
                if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()), new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()));
                    Toast.makeText(context, "Download success!", Toast.LENGTH_SHORT).show();
                } else {
                    downloadFile(wallpaper);
                }
                holder.imageDownload.setVisibility(View.GONE);
            }
        });

        holder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(wallpaper.isLike() ? R.drawable.ic_live_false : R.drawable.ic_live_true).into(holder.imageLike);
                wallpaper.setLike(!wallpaper.isLike());

                databaseHelper.updateWallpaper(wallpaper);
            }
        });

        holder.imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper = false;
                isShare = true;
                if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_VIDEO)) {
                    Utilities.shareFile(wallpaper.getUri(), context);
                } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()), new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()));
                    Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).getPath(), context);
                } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).exists()) {
                    Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).getPath(), context);
                } else
                    downloadFile(wallpaper);
            }
        });
    }

    private void downloadFile(Wallpaper wallpaper) {
        new DownloadFile(this).execute(wallpaper.getUri(), Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName());
    }

    private void setWallpaper() {
        VideoLiveWallpaper.setToWallPaper(context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onTaskCompleted(String fileName) {
        if (setWallpaper) {
            Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, fileName), new File(context.getFilesDir() + "/file_wallpapers.mp4"));
            setWallpaper();
        } else if (isShare) {
            Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, fileName).getPath(), context);
        } else {
            Toast.makeText(context, "Download success!", Toast.LENGTH_SHORT).show();
        }

        MediaScannerConnection.scanFile(context,
                new String[]{Environment.getExternalStorageDirectory().toString()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ImageView imageLike, imageShare, imageDownload, imageViewSetAs;
        ProgressBar progressBar;
        SeekBar seekBarTime;
        private Handler mHandler = new Handler();

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            imageLike = itemView.findViewById(R.id.imageLike);
            imageShare = itemView.findViewById(R.id.imageShare);
            imageDownload = itemView.findViewById(R.id.imageDownload);
            imageViewSetAs = itemView.findViewById(R.id.imageSetWallpaper);
            progressBar = itemView.findViewById(R.id.progressBar);
            seekBarTime = itemView.findViewById(R.id.seekBarTime);

            seekBarTime.setPadding(0, 0, 0, 0);
            seekBarTime.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        void setDataVideo(Wallpaper wallpaper) {
            if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_VIDEO)) {
                videoView.setVideoPath(wallpaper.getUri());
            } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).exists()) {
                videoView.setVideoPath(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_CACHE, wallpaper.getName()).getPath());
            } else if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).exists()) {
                videoView.setVideoPath(new File(Utilities.ROOT_DIR_STORAGE_VIDEO, wallpaper.getName()).getPath());
            } else
                videoView.setVideoPath(wallpaper.getUri());


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);


                    float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                    float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                    float scale = videoRatio / screenRatio;
                    if (scale >= 1f) {
                        videoView.setScaleX(scale);
                    } else {
                        videoView.setScaleY(1f / scale);
                    }

                    updateProgressBar();
                    mediaPlayer.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }

        private void updateProgressBar() {
            mHandler.postDelayed(updateTimeTask, 100);
        }

        private Runnable updateTimeTask = new Runnable() {
            public void run() {
                seekBarTime.setProgress(videoView.getCurrentPosition());
                seekBarTime.setMax(videoView.getDuration());
                mHandler.postDelayed(this, 50);
            }
        };
    }
}
