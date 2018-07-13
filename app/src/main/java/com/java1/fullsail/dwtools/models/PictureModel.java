package com.java1.fullsail.dwtools.models;

import android.net.Uri;

import java.io.Serializable;

public class PictureModel implements Serializable {
    Uri path;

    public PictureModel(Uri path) {
        this.path = path;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }
}
