package com.example.photo_wallpapers.PreviewPictureActivity;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_wallpapers.Data.DatabaseHelper;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.PreviewCustomPictureActivity.PreviewCustomPictureActivity;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.Constant;
import com.example.photo_wallpapers.Util.DownloadFile;
import com.example.photo_wallpapers.Util.OnTaskCompleted;
import com.example.photo_wallpapers.Util.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PreviewPictureAdapter extends RecyclerView.Adapter<PreviewPictureAdapter.ViewHolder> implements OnTaskCompleted {

    private List<Wallpaper> data;
    private Context context;
    private boolean isStorage;
    private boolean setWallpaper = false;
    private boolean isShare = false;
    private DatabaseHelper databaseHelper;
    private Bitmap[] bitmapImage = new Bitmap[1];


    public PreviewPictureAdapter(Context context, List<Wallpaper> data, boolean isStorage) {
        this.context = context;
        this.data = data;
        this.isStorage = isStorage;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView =
                inflater.inflate(R.layout.item_picture_preview, parent, false);

        return new PreviewPictureAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Wallpaper wallpaper = data.get(position);

        holder.setImage(wallpaper, bitmapImage);
        holder.imageLike.setVisibility(isStorage ? View.GONE : View.VISIBLE);
        holder.imageDownload.setVisibility(isStorage ? View.GONE : (new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()).exists() ? View.GONE : View.VISIBLE));

        Picasso.get().load(wallpaper.isLike() ? R.drawable.ic_live_true : R.drawable.ic_live_false).into(holder.imageLike);

        holder.imageViewSetAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setWallpaper = true;
                isShare = false;
                if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_PICTURE)) {
                    setWallpaper(new File(wallpaper.getUri()));
                } else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()), new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()));
                    setWallpaper(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()));
                } else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()).exists()) {
                    setWallpaper(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()));
                } else
                    downloadFile(wallpaper);
            }
        });

        holder.imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper = false;
                isShare = false;
                if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()), new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()));
                    Toast.makeText(context, "Download success!", Toast.LENGTH_SHORT).show();
                } else
                    downloadFile(wallpaper);

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
                if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_PICTURE)) {
                    Utilities.shareFile(wallpaper.getUri(), context);
                } else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()).exists()) {
                    Utilities.copyFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getName()), new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()));
                    Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()).getPath(), context);
                } else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()).exists()) {
                    Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName()).getPath(), context);
                } else
                    downloadFile(wallpaper);
            }
        });
    }

    private void downloadFile(Wallpaper wallpaper) {
        new DownloadFile(this).execute(wallpaper.getUri(), Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getName());
    }


    private void setWallpaper(File image) {

        int imageHeight = bitmapImage[0].getHeight();
        int imageWidth = bitmapImage[0].getWidth();

        Bitmap bitmapScrop = null;
        if (((float) imageWidth / imageHeight) >= 0.65f) {
            bitmapScrop = Bitmap.createBitmap(bitmapImage[0], (int) ((imageWidth - (imageHeight * (1080 / 1920.0))) / 2.0), 0, (int) (imageHeight * (1080 / 1920.0)), imageHeight);
        } else if (((float) imageWidth / imageHeight) <= 0.4f) {
            bitmapScrop = Bitmap.createBitmap(bitmapImage[0], 0, (int) ((imageHeight - (imageWidth / (1080 / 1920.0))) / 2.0), imageWidth, (int) (imageWidth / (1080 / 1920.0)));
        }

        if (bitmapScrop != null) {
            File fileroot = new File(Utilities.ROOT_DIR_STORAGE_PICTURE, "crop_size_image.png");
            try (FileOutputStream out = new FileOutputStream(fileroot)) {
                bitmapScrop.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        InputStream in = null;
        try {
            in = new FileInputStream(bitmapScrop != null ? new File(Utilities.ROOT_DIR_STORAGE_PICTURE, "crop_size_image.png") : image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (in != null) {
            try {
                FileOutputStream fos = context.openFileOutput(
                        Constant.CACHE, Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = in.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytes);
                }
                in.close();
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.toast_failed_set_picture,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, R.string.toast_invalid_pic_path,
                    Toast.LENGTH_LONG).show();
        }

        try {

            ((PreviewPictureActivity)context).startActivity(new Intent(context, PreviewCustomPictureActivity.class));

            WallpaperManager.getInstance(context).clear();

        } catch (Exception e) {
            try {
                context.startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(context, e2.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onTaskCompleted(String nameFile) {
        if (setWallpaper) {
//            setWallpaper();
        } else if (isShare) {
            Utilities.shareFile(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, nameFile).getPath(), context);
        } else {
            Toast.makeText(context, "Download success!", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(context,
                    new String[]{Environment.getExternalStorageDirectory().toString()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPicture, imageLike, imageShare, imageDownload, imageViewSetAs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPicture = itemView.findViewById(R.id.imageviewPicture);
            imageLike = itemView.findViewById(R.id.imageLike);
            imageShare = itemView.findViewById(R.id.imageShare);
            imageDownload = itemView.findViewById(R.id.imageDownload);
            imageViewSetAs = itemView.findViewById(R.id.imageSetWallpaper);
        }

        void setImage(Wallpaper wallpaper, Bitmap[] bitmapImage) {

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    bitmapImage[0] = bitmap;
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            imageViewPicture.setTag(target);

            if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getThum()).exists()) {
                Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getThum())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(target);
                Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getThum())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(imageViewPicture);
            } else if (wallpaper.getType().equals(EnumTypeWallPaper.STORAGE_PICTURE)) {
                Picasso.get().load(new File(wallpaper.getUri())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(target);
                Picasso.get().load(new File(wallpaper.getUri())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(imageViewPicture);
            } else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getThum()).exists()) {
                Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getThum())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(target);
                Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getThum())).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(imageViewPicture);
            } else {
                Picasso.get().load(wallpaper.getUri()).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(target);
                Picasso.get().load(wallpaper.getUri()).resize(0, 1920)
                        .onlyScaleDown().centerCrop().into(imageViewPicture);
            }
        }
    }
}
