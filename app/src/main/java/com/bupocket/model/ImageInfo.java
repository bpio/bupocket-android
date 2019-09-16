package com.bupocket.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ImageInfo {


    private String title;

    private String url;
    @Id
    private String imageUrl;

    @Generated(hash = 857391052)
    public ImageInfo(String title, String url, String imageUrl) {
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    @Generated(hash = 2139894022)
    public ImageInfo() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
