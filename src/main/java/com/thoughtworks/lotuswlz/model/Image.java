package com.thoughtworks.lotuswlz.model;

import java.io.Serializable;

/**
 * Created by lzwu on 9/28/14.
 */
public class Image implements Serializable {
    private int imageId;
    private String imageUrl;

    public Image() {

    }

    public Image(int imageId, String imageUrl) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "imageId=" + this.imageId + ";imageUrl=" + this.imageUrl;
    }
}
