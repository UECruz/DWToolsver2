package com.java1.fullsail.dwtools.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class Inventory implements Serializable{

    private Bitmap images;
    private Uri uri;
    private String data;

    Inventory(){
        super();
    }

    Inventory(Uri uri) {
        this.uri = uri;
    }

    public String getData(){
        return data;
    }

    public void setData(String x){
        this.data = x;
    }

    public Bitmap getImages(){
        return images;
    }

    public void setImages(Bitmap images){
        this.images = images;
    }

    Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
