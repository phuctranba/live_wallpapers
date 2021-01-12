package com.example.photo_wallpapers.PreviewPictureActivity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.photo_wallpapers.PreviewCustomPictureActivity.PreviewCustomPictureActivity;
import com.example.photo_wallpapers.R;
import com.example.photo_wallpapers.Util.LiveWallpaperService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SetAsFragment extends BottomSheetDialogFragment {

    LinearLayout btnHome, btnLock, btnHomeandLock, btnCancel;
    Context context;
    String imageSource;

    public SetAsFragment(String imageSource, Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnHome = view.findViewById(R.id.btnHome);
        btnLock = view.findViewById(R.id.btnLock);
        btnHomeandLock = view.findViewById(R.id.btnHomeandLock);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                Intent intent = new Intent(context, PreviewCustomPictureActivity.class);
                intent.putExtra("TYPE", "HOME");
                startActivity(intent);
            }
        });

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                try {
                    startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new
                                    ComponentName(getContext(), LiveWallpaperService.class))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }catch(Exception e){
                    startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        btnHomeandLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

//                Intent intent = new Intent(context, PreviewCustomPictureActivity.class);
//                intent.putExtra("TYPE", "HOMEANDLOCK");
//                intent.putExtra("SOURCE", imageSource);
//                startActivity(intent);

                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(context, PreviewCustomPictureActivity.class));
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.set_as_fragment_bottom_dialog, container, false);
    }


}