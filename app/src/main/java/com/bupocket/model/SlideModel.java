package com.bupocket.model;

import java.util.ArrayList;

public class SlideModel {


    ArrayList<ImageInfo> slideshow;

    public ArrayList<ImageInfo> getSlideshow() {
        return slideshow;
    }

    public void setSlideshow(ArrayList<ImageInfo> slideshow) {
        this.slideshow = slideshow;
    }

    public class  ImageInfo{

        private String title;
        private String url;
        private String imageUrl;

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

}
