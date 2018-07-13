//Ursula Cruz
//Framework 3
//Images
package com.java1.fullsail.dwtools.models;


import android.graphics.Bitmap;
import android.net.Uri;

public class Images {

    private Bitmap images;
    private Uri uri;

    public Images(){
        super();
    }

    Images(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getImages(){
        return images;
    }

    public void setImages(Bitmap images){
        this.images = images;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
