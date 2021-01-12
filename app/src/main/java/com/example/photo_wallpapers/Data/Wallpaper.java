package com.example.photo_wallpapers.Data;

import java.io.Serializable;

public class Wallpaper implements Serializable {

    public static final String URI_VIDEO = "https://bybit.vn/livephotos";
    public static final String URI_PICTURE = "https://bybit.vn/photos";

    private String id, uri, name, thum;
    private EnumTypeWallPaper type;
    private boolean like;

    public Wallpaper() {
    }

    public Wallpaper(String id, String uri, String thum, String name, EnumTypeWallPaper type, boolean like) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.type = type;
        this.like = like;
        this.thum = thum;
    }

    public String getThum() {
        return thum;
    }

    public void setThum(String thum) {
        this.thum = thum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public EnumTypeWallPaper getType() {
        return type;
    }

    public void setType(EnumTypeWallPaper type) {
        this.type = type;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

}
