package com.example.photo_wallpapers.ListOptionsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photo_wallpapers.Data.EnumTypeWallPaper;
import com.example.photo_wallpapers.Data.Wallpaper;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.Utilities;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Wallpaper> data;
    private Context context;
    private ItemWallpaperListener itemWallpaperListener;

    public ItemAdapter(Context context, List<Wallpaper> data, ItemWallpaperListener itemWallpaperListener) {
        this.context = context;
        this.data = data;
        this.itemWallpaperListener = itemWallpaperListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView =
                inflater.inflate(R.layout.item_wallpaper, parent, false);

        return new ViewHolder(itemView, itemWallpaperListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Wallpaper wallpaper = data.get(position);
        if (wallpaper == null) {
            holder.viewOverlay.setVisibility(View.VISIBLE);
            holder.imageLike.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.beach_custom).resize(0, 1920)
                    .onlyScaleDown().centerCrop().into(holder.image);
        } else {
            holder.viewOverlay.setVisibility(View.GONE);

            if (wallpaper.getType().equals(EnumTypeWallPaper.PICTURE) || wallpaper.getType().equals(EnumTypeWallPaper.VIDEO)) {
                holder.imageLike.setVisibility(View.VISIBLE);
                Picasso.get().load(wallpaper.isLike() ? R.drawable.ic_live_true : R.drawable.ic_live_false).into(holder.imageLike);

                if (wallpaper.getType().equals(EnumTypeWallPaper.VIDEO)) {
                    if (new File(Utilities.ROOT_DIR_STORAGE_VIDEO_THUMB_CACHE, wallpaper.getThum()).exists()) {
                        Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_VIDEO_THUMB_CACHE, wallpaper.getThum())).resize(0, 1920)
                                .onlyScaleDown().centerCrop().into(holder.image);
                    } else {
                        if (wallpaper.getThum() != null && !wallpaper.getThum().equals(""))
                            Picasso.get().load(Wallpaper.URI_VIDEO + "/" + wallpaper.getThum()).resize(0, 1920)
                                    .onlyScaleDown().centerCrop().into(holder.image);
                        else
                            Glide.with(context)
                                    .asBitmap()
                                    .load(wallpaper.getUri())
                                    .centerCrop()
                                    .into(holder.image);

                    }
                }else {
                    if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getThum()).exists()){
                        Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE_CACHE, wallpaper.getThum())).resize(0, 1920)
                                .onlyScaleDown().centerCrop().into(holder.image);
                    }else if (new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getThum()).exists()){
                        Picasso.get().load(new File(Utilities.ROOT_DIR_STORAGE_PICTURE, wallpaper.getThum())).resize(0, 1920)
                                .onlyScaleDown().centerCrop().into(holder.image);
                    } else {
                        Picasso.get().load(wallpaper.getUri()).resize(0, 1920)
                                .onlyScaleDown().centerCrop().into(holder.image);
                    }
                }
            } else {
                holder.imageLike.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(wallpaper.getThum())
                        .centerCrop()
                        .into(holder.image);
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout viewOverlay;
        SelectableRoundedImageView image;
        ImageView imageLike;
        ItemWallpaperListener itemWallpaperListener;

        public ViewHolder(View itemView, ItemWallpaperListener itemWallpaperListener) {
            super(itemView);
            viewOverlay = itemView.findViewById(R.id.viewOverlay);
            image = itemView.findViewById(R.id.image);
            imageLike = itemView.findViewById(R.id.likeImage);
            this.itemWallpaperListener = itemWallpaperListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemWallpaperListener.itemWallpaperClick(getAdapterPosition());
        }
    }

    public interface ItemWallpaperListener {
        void itemWallpaperClick(int position);
    }
}
